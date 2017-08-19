#ifdef GL_ES
precision mediump float;
#endif

#define M_PI 3.14159265359
#define MIN_ATTENUATION 0.0001
#define ATTENUATION_THRESHOLD 0.01

uniform int u_graphicsWidth;
uniform int u_graphicsHeight;
uniform mat4 u_viewMatrix;

#ifdef lights
    #if lights > 0
        struct Light {
            int type;
            vec3 position;
            vec3 direction;
            float intensity;
            float innerAngle;
            float outerAngle;
        };

        uniform sampler2D u_lights;
        uniform sampler2D u_lightInfo;
        uniform Light u_lightsList[lights];
    #endif
#endif

#ifdef defAlbedo
    uniform sampler2D u_albedo;
#endif

#ifdef defMetalness
    uniform sampler2D u_metalness;
#endif

#ifdef defRoughness
    uniform sampler2D u_roughness;
#endif

#ifdef defNormal
    uniform sampler2D u_normal;
#endif

#ifdef defAmbientOcclusion
    uniform sampler2D u_ambientOcclusion;
#endif

#ifdef defImageBasedLighting
    uniform samplerCube u_irradiance;
    uniform samplerCube u_prefilter;
    uniform sampler2D u_brdf;
#endif

varying vec2 v_texCoord;
varying vec3 v_position;
varying vec3 v_normal;
varying vec3 v_binormal;
varying vec3 v_tangent;

vec3 fresnelSchlick(float cosTheta, vec3 F0)
{
    return F0 + (1.0 - F0) * pow(1.0 - cosTheta, 5.0);
}

vec3 fresnelSchlickRoughness(float cosTheta, vec3 F0, float roughness)
{
    return F0 + (max(vec3(1.0 - roughness), F0) - F0) * pow(1.0 - cosTheta, 5.0);
}

float DistributionGGX(vec3 N, vec3 H, float rough) {
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

float GeometrySchlickGGX(float NdotV, float rough) {
    float r = (rough + 1.0);
    float k = (r*r) / 8.0;

    float nom = NdotV;

    float denom = NdotV * (1.0 - k) + k;
    denom = max(0.0001, denom);

    return nom / denom;
}
float GeometrySmith(vec3 N, vec3 V, vec3 L, float rough) {
    float NdotV = max(dot(N, V), 0.0);
    float NdotL = max(dot(N, L), 0.0);
    float ggx2 = GeometrySchlickGGX(NdotV, rough);
    float ggx1 = GeometrySchlickGGX(NdotL, rough);

    return ggx1 * ggx2;
}

void main() {
    #ifdef defAlbedo
        vec3 albedo = texture2D(u_albedo, v_texCoord).rgb;
    #else
        vec3 albedo = vec3(0.5);
    #endif

    #ifdef defMetalness
        float metalness = texture2D(u_metalness, v_texCoord).r;
    #else
        float metalness = 1.0;
    #endif

    #ifdef defRoughness
        float roughness = texture2D(u_roughness, v_texCoord).r;
    #else
        float roughness = 0.5;
    #endif

    #ifdef defAmbientOcclusion
        float ambientOcclusion = texture2D(u_ambientOcclusion, v_texCoord).r;
    #else
        float ambientOcclusion = 1.0;
    #endif

    #ifdef defNormal
        vec3 normalMap = texture2D(u_normal, v_texCoord).rgb;

        vec3 N = normalize(v_normal);
        vec3 tangent = normalize(v_tangent);
        vec3 bitangent = cross(tangent, N);

        tangent = normalize(tangent - dot(tangent, N) * N);
        mat3 TBN = mat3(tangent, bitangent, N);

        N = normalize(TBN * (normalMap * 2.0 - 1.0));
    #else
        vec3 N = normalize(v_normal);
    #endif

    vec3 V = normalize(-v_position);
    vec3 R = reflect(-V, N);

    vec3 Lo = vec3(0.0);
    vec3 F0 = vec3(0.04);
    F0 = mix(F0, albedo, metalness);

    #ifdef lights
        int tileX = int(gl_FragCoord.x) / (u_graphicsWidth / 10);
        int tileY = int(gl_FragCoord.y) / (u_graphicsHeight / 10);

        float textureRow = float(tileY * 10 + tileX) / 128.0;

        vec4 pixel = texture2D(u_lights, vec2(0.5 / 64.0, textureRow));
        int pixelCeil = int(ceil(pixel.r * 255.0));

        for (int i = 0; i < pixelCeil; i++) {
            vec4 tempPixel = texture2D(u_lights, vec2((float(i) + 1.5) / 64.0, textureRow));

            int lightId = int(clamp(ceil(tempPixel.r * 255.0), 0.0, 255.0));
            Light light = u_lightsList[lightId];

            vec4 lightInfo = texture2D(u_lightInfo, vec2(0.5, float(lightId) / 128.0));
            float lightRadius = lightInfo.a * 255.0;
            lightInfo.a = 1.0;

            vec3 lightDirection = light.position - v_position;
            float lightDistance = length(lightDirection);

            float lightAttenuation = clamp(1.0 - (lightDistance / lightRadius), 0.0, 1.0);
            lightAttenuation *= lightAttenuation;

            if (light.type == 0) {
                lightDirection = normalize(-light.direction);
                lightDistance = length(lightDirection);
                lightAttenuation = 1.0;
            }

            vec3 L = normalize(lightDirection);
            vec3 H = normalize(V + L);

            vec3 radiance = lightInfo.rgb;

            if (light.type == 2) {
                float currentAngle = dot(-normalize(lightDirection), normalize(light.direction));
                float innerConeAngle = cos(radians(light.innerAngle));
                float outerConeAngle = cos(radians(light.outerAngle));
                float conesAngleDiff = abs(innerConeAngle - outerConeAngle);

                float spotEffect = clamp((currentAngle - outerConeAngle) / conesAngleDiff, 0.0, 1.0);
                radiance *= spotEffect;
            }

            vec3 F = fresnelSchlick(max(dot(H, V), 0.0), F0);
            float NDF = DistributionGGX(N, H, roughness);
            float G = GeometrySmith(N, V, L, roughness);

            vec3 nominator = NDF * G * F;
            float denominator = 4.0 * max(dot(N, V), 0.0) * max(dot(N, L), 0.0) + 0.001;
            vec3 brdf = nominator / denominator;

            vec3 kS = F;
            vec3 kD = vec3(1.0) - kS;

            kD *= 1.0 - metalness;

            float NdotL = max(dot(N, L), 0.0) * light.intensity;
            Lo += (kD * albedo / M_PI + brdf) * radiance * NdotL * lightAttenuation;
        }
    #endif

    #ifdef defImageBasedLighting
        vec3 F = fresnelSchlickRoughness(max(dot(N, V), 0.0), F0, roughness);

        vec3 kS = F;
        vec3 kD = 1.0 - kS;
        kD *= 1.0 - metalness;

        vec3 irradiance = textureCube(u_irradiance, N).rgb;
        vec3 diffuse = irradiance * albedo;

        const float MAX_REFLECTION_LOD = 4.0;

        vec3 prefilteredColor = textureCubeLod(u_prefilter, R, roughness * MAX_REFLECTION_LOD).rgb;
        vec2 brdf = texture2D(u_brdf, vec2(max(dot(N, V), 0.0), roughness)).rg;
        vec3 specular = prefilteredColor * (F * brdf.x + brdf.y);

        vec3 ambient = (kD * diffuse + specular) * ambientOcclusion;
    #else
        vec3 ambient = vec3(0.03) * albedo;
    #endif

    vec3 color = ambient + Lo;

    #ifdef defGammaCorrection
        color = color / (color + vec3(1.0));
        color = pow(color, vec3(1.0 / 2.2));
    #endif

    gl_FragColor = vec4(color, 1.0);
}
