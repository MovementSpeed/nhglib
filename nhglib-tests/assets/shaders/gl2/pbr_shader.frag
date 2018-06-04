#ifdef GL_ES
    #ifdef GPU_MALI
        #define LOWP lowp
        #define MEDP mediump
        #define HIGHP highp
    #elif defined GPU_ADRENO
        #define LOWP
        #define MEDP
        #define HIGHP
    #else
        #define MEDP
        #define LOWP
        #define HIGHP
    #endif

    precision mediump float;
#else
    #define MEDP
    #define LOWP
    #define HIGHP
#endif

#define M_PI 3.14159265359
#define MAX_REFLECTION_LOD 4.0

uniform LOWP float u_ambient;
uniform LOWP int u_graphicsWidth;
uniform LOWP int u_graphicsHeight;
uniform HIGHP mat4 u_viewMatrix;

#ifdef numDirectionalLights
    #if numDirectionalLights > 0
        struct DirectionalLight {
            LOWP vec3 color;
            LOWP vec3 direction;
            LOWP float intensity;
        };

        uniform DirectionalLight u_dirLights[numDirectionalLights];
    #endif
#endif

#ifdef numPointLights
    #if numPointLights > 0
        struct PointLight {
            LOWP vec3 color;
            LOWP vec3 position;
            LOWP float intensity;
            LOWP float radius;
        };

        uniform PointLight u_pointLights[numPointLights];
    #endif
#endif

#ifdef numSpotLights
    #if numSpotLights > 0
        struct SpotLight {
            LOWP vec3 color;
            LOWP vec3 position;
            LOWP vec3 direction;
            LOWP float intensity;
            LOWP float innerAngle;
            LOWP float outerAngle;
        };

        uniform SpotLight u_spotLights[numSpotLights];
    #endif
#endif

#ifdef defAlbedo
    uniform LOWP sampler2D u_albedo;
#endif

#ifdef defMetalness
    uniform LOWP sampler2D u_metalness;
#endif

#ifdef defRoughness
    uniform LOWP sampler2D u_roughness;
#endif

#ifdef defNormal
    uniform LOWP sampler2D u_normal;
#endif

#ifdef defAmbientOcclusion
    uniform LOWP sampler2D u_ambientOcclusion;
#endif

#ifdef defImageBasedLighting
    uniform LOWP samplerCube u_irradiance;
    uniform LOWP samplerCube u_prefilter;
    uniform LOWP sampler2D u_brdf;
#endif

varying HIGHP vec3 v_position;
varying HIGHP vec3 v_binormal;
varying HIGHP vec3 v_tangent;
varying LOWP vec2 v_texCoord;
varying LOWP vec3 v_normal;

vec3 fresnelSchlick(float cosTheta, vec3 F0)
{
    return F0 + (1.0 - F0) * pow(1.0 - cosTheta, 5.0);
}

vec3 fresnelSchlickRoughness(float cosTheta, vec3 F0, float roughness)
{
    return F0 + (max(vec3(1.0 - roughness), F0) - F0) * pow(1.0 - cosTheta, 5.0);
}

float distributionGGX(vec3 N, vec3 H, float rough) {
    float a = rough*rough;
    float a2 = a*a;
    float NdotH = max(dot(N, H), 0.0);
    float NdotH2 = NdotH*NdotH;

    float nom = a2;

    float denom = (NdotH2 * (a2 - 1.0) + 1.0);
    denom = M_PI * denom * denom;
    denom = max(0.0001, denom);

    return nom / denom;
}

float geometrySchlickGGX(float NdotV, float rough) {
    float r = (rough + 1.0);
    float k = (r*r) / 8.0;

    float nom = NdotV;

    float denom = NdotV * (1.0 - k) + k;
    denom = max(0.0001, denom);

    return nom / denom;
}

float geometrySmith(vec3 N, vec3 V, vec3 L, float rough) {
    float NdotV = max(dot(N, V), 0.0);
    float NdotL = max(dot(N, L), 0.0);
    float ggx2 = geometrySchlickGGX(NdotV, rough);
    float ggx1 = geometrySchlickGGX(NdotL, rough);

    return ggx1 * ggx2;
}

vec3 saturation(vec3 rgb, float adjustment) {
    const vec3 W = vec3(0.2125, 0.7154, 0.0721);
    vec3 intensity = vec3(dot(rgb, W));
    return mix(intensity, rgb, adjustment);
}

void main() {
    // Nota: NON SETTARE MAI METALNESS O ROUGHNESS A 0.0
    LOWP vec3 color;

    #ifdef defAlbedo
        LOWP vec4 albedo = texture2D(u_albedo, v_texCoord);
    #else
        LOWP vec4 albedo = vec4(1.0);
    #endif

    #ifdef defMetalness
        LOWP float metalness = texture2D(u_metalness, v_texCoord).r;
    #else
        LOWP float metalness = 0.5;
    #endif

    #ifdef defRoughness
        LOWP float roughness = texture2D(u_roughness, v_texCoord).r;
    #else
        LOWP float roughness = 0.1;
    #endif

    #ifdef defAmbientOcclusion
        LOWP float ambientOcclusion = texture2D(u_ambientOcclusion, v_texCoord).r;
    #else
        LOWP float ambientOcclusion = 1.0;
    #endif

    #ifdef defNormal
        LOWP vec3 normalMap = texture2D(u_normal, v_texCoord).rgb;

        LOWP vec3 N = normalize(v_normal);
        LOWP vec3 tangent = normalize(v_tangent);
        LOWP vec3 bitangent = cross(tangent, N);

        tangent = normalize(tangent - dot(tangent, N) * N);
        LOWP mat3 TBN = mat3(tangent, bitangent, N);

        N = normalize(TBN * (normalMap * 2.0 - 1.0));
    #else
        LOWP vec3 N = normalize(v_normal);
    #endif

    LOWP vec3 V = normalize(-v_position);

    LOWP vec3 F0 = vec3(0.04);
    F0 = mix(F0, albedo, metalness);

    LOWP vec3 Lo = vec3(0.0);

    // Directional lights
    #ifdef numDirectionalLights
        #if numDirectionalLights > 0
            for (int i = 0; i < numDirectionalLights; i++) {
                LOWP vec3 direction = -u_dirLights[i].direction;
                LOWP float distance = length(direction);
                LOWP vec3 radiance = u_dirLights[i].color;

                LOWP vec3 L = normalize(direction);
                LOWP vec3 H = normalize(V + L);

                // cook-torrance brdf
                LOWP float NDF = distributionGGX(N, H, roughness);
                LOWP float G = geometrySmith(N, V, L, roughness);
                LOWP vec3 F = fresnelSchlick(max(dot(H, V), 0.0), F0);

                LOWP vec3 kS = F;
                LOWP vec3 kD = vec3(1.0) - kS;
                kD *= 1.0 - metalness;

                LOWP vec3 numerator = NDF * G * F;
                LOWP float denominator = 4.0 * max(dot(N, V), 0.0) * max(dot(N, L), 0.0);
                LOWP vec3 specular = numerator / max(denominator, 0.001);

                // add to outgoing radiance Lo
                LOWP float NdotL = max(dot(N, L), 0.0) * u_dirLights[i].intensity;
                Lo += (kD * albedo.rgb / M_PI + specular) * radiance * NdotL;
            }
        #endif
    #endif

    // Point lights
    #ifdef numPointLights
        #if numPointLights > 0
            for (int i = 0; i < numPointLights; i++) {
                LOWP vec3 direction = u_pointLights[i].position - v_position;
                LOWP float distance = length(direction);
                LOWP float attenuation = 1.0 / (distance * distance);
                LOWP vec3 radiance = u_pointLights[i].color * attenuation;

                LOWP vec3 L = normalize(direction);
                LOWP vec3 H = normalize(V + L);

                // cook-torrance brdf
                LOWP float NDF = distributionGGX(N, H, roughness);
                LOWP float G = geometrySmith(N, V, L, roughness);
                LOWP vec3 F = fresnelSchlick(max(dot(H, V), 0.0), F0);

                LOWP vec3 kS = F;
                LOWP vec3 kD = vec3(1.0) - kS;
                kD *= 1.0 - metalness;

                LOWP vec3 numerator = NDF * G * F;
                LOWP float denominator = 4.0 * max(dot(N, V), 0.0) * max(dot(N, L), 0.0);
                LOWP vec3 specular = numerator / max(denominator, 0.001);

                // add to outgoing radiance Lo
                LOWP float NdotL = max(dot(N, L), 0.0) * u_pointLights[i].intensity;
                Lo += (kD * albedo.rgb / M_PI + specular) * radiance * NdotL;
            }
        #endif
    #endif

    // Spot lights
    #ifdef numSpotLights
        #if numSpotLights > 0
            for (int i = 0; i < numPointLights; i++) {
                LOWP vec3 direction = u_spotLights[i].position - v_position;
                LOWP float distance = length(direction);
                LOWP float attenuation = 1.0 / (distance * distance);
                LOWP vec3 radiance = u_spotLights[i].color * attenuation;

                float currentAngle = dot(-normalize(direction), normalize(u_spotLights[i].direction));
                float innerConeAngle = cos(radians(u_spotLights[i].innerAngle));
                float outerConeAngle = cos(radians(u_spotLights[i].outerAngle));
                float conesAngleDiff = abs(innerConeAngle - outerConeAngle);

                float spotEffect = clamp((currentAngle - outerConeAngle) / conesAngleDiff, 0.0, 1.0);
                radiance *= spotEffect;

                LOWP vec3 L = normalize(direction);
                LOWP vec3 H = normalize(V + L);

                // cook-torrance brdf
                LOWP float NDF = distributionGGX(N, H, roughness);
                LOWP float G = geometrySmith(N, V, L, roughness);
                LOWP vec3 F = fresnelSchlick(max(dot(H, V), 0.0), F0);

                LOWP vec3 kS = F;
                LOWP vec3 kD = vec3(1.0) - kS;
                kD *= 1.0 - metalness;

                LOWP vec3 numerator = NDF * G * F;
                LOWP float denominator = 4.0 * max(dot(N, V), 0.0) * max(dot(N, L), 0.0);
                LOWP vec3 specular = numerator / max(denominator, 0.001);

                // add to outgoing radiance Lo
                LOWP float NdotL = max(dot(N, L), 0.0) * u_spotLights[i].intensity;
                Lo += (kD * albedo.rgb / M_PI + specular) * radiance * NdotL;
            }
        #endif
    #endif

    color = (vec3(u_ambient) * albedo.rgb) + Lo;

    #ifdef defGammaCorrection
        color = color / (color + vec3(1.0));
        color = pow(color, vec3(1.0 / 2.2));
    #endif

    gl_FragColor = vec4(color, albedo.a);
}