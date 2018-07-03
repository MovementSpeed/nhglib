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

out vec4 fragmentColor;

uniform LOWP float u_ambient;
uniform LOWP int u_graphicsWidth;
uniform LOWP int u_graphicsHeight;
uniform HIGHP mat4 u_viewMatrix;

#ifdef lights
    #if lights > 0
        uniform LOWP sampler2D u_lights;
        uniform LOWP sampler2D u_lightInfo;

        uniform LOWP int u_lightTypes[lights];
        uniform LOWP vec2 u_lightAngles[lights];
        uniform LOWP vec4 u_lightPositionsAndRadiuses[lights];
        uniform LOWP vec4 u_lightDirectionsAndIntensities[lights];
    #endif
#endif

#ifdef defAlbedo
    uniform LOWP sampler2D u_albedo;
    uniform LOWP vec2 u_albedoTiles;
#endif

#ifdef defMetalness
    uniform LOWP sampler2D u_metalness;
    uniform LOWP vec2 u_metalnessTiles;
#endif

#ifdef defRoughness
    uniform LOWP sampler2D u_roughness;
    uniform LOWP vec2 u_roughnessTiles;
#endif

#ifdef defNormal
    uniform LOWP sampler2D u_normal;
    uniform LOWP vec2 u_normalTiles;
#endif

#ifdef defAmbientOcclusion
    uniform LOWP sampler2D u_ambientOcclusion;
    uniform LOWP vec2 u_ambientOcclusionTiles;
#endif

#ifdef defImageBasedLighting
    uniform LOWP samplerCube u_irradiance;
    uniform LOWP samplerCube u_prefilter;
    uniform LOWP sampler2D u_brdf;
#endif

in HIGHP vec3 v_position;
in HIGHP vec3 v_binormal;
in HIGHP vec3 v_tangent;
in LOWP vec2 v_texCoord;
in LOWP vec3 v_normal;

vec3 fresnelSchlick(float cosTheta, vec3 F0) {
    return F0 + (1.0 - F0) * pow(1.0 - cosTheta, 5.0);
}

vec3 fresnelSchlickRoughness(float cosTheta, vec3 F0, float roughness) {
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

    #ifdef defAlbedo
        LOWP vec4 albedo = texture(u_albedo, fract(v_texCoord / u_albedoTiles));
        if (albedo.a < 0.01) discard;
        albedo = pow(albedo, vec4(2.2));
    #else
        LOWP vec4 albedo = vec4(1.0);
    #endif

    LOWP vec3 color;

    #ifdef defMetalness
        LOWP float metalness = texture(u_metalness, fract(v_texCoord / u_metalnessTiles)).r;
    #else
        LOWP float metalness = 0.01;
    #endif

    #ifdef defRoughness
        LOWP float roughness = texture(u_roughness, fract(v_texCoord / u_roughnessTiles)).r;
    #else
        LOWP float roughness = 0.8;
    #endif

    #ifdef defAmbientOcclusion
        LOWP float ambientOcclusion = texture(u_ambientOcclusion, fract(v_texCoord / u_ambientOcclusionTiles)).r;
    #else
        LOWP float ambientOcclusion = 1.0;
    #endif

    #ifdef defNormal
        LOWP vec3 normalMap = texture(u_normal, fract(v_texCoord / u_normalTiles)).rgb;

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
    LOWP vec3 R = reflect(-V, N);
    R = vec3(inverse(u_viewMatrix) * vec4(R, 0.0));

    LOWP vec3 F0 = vec3(0.04);
    F0 = mix(F0, albedo.rgb, metalness);

    LOWP vec3 Lo = vec3(0.0);

    #ifdef lights
        LOWP int tileX = int(gl_FragCoord.x) / (u_graphicsWidth / GRID_SIZE);
        LOWP int tileY = int(gl_FragCoord.y) / (u_graphicsHeight / GRID_SIZE);

        LOWP float textureRow = float(tileY * GRID_SIZE + tileX) / 128.0;
        LOWP vec4 pixel = texture(u_lights, vec2(0.5 / 64.0, textureRow));

        for (int i = 0; i < int(ceil(pixel.r * 255.0)); i++) {
            LOWP vec4 tempPixel = texture(u_lights, vec2((float(i) + 1.5) / 64.0, textureRow));

            LOWP int lightId = int(clamp(ceil(tempPixel.r * 255.0), 0.0, 255.0));

            LOWP vec4 lightInfo = texture(u_lightInfo, vec2(0.5, float(lightId) / 128.0));
            LOWP float lightRadius = u_lightPositionsAndRadiuses[lightId].w;

            LOWP vec3 lightDirection = u_lightPositionsAndRadiuses[lightId].xyz - v_position;
            LOWP float lightDistance = length(lightDirection);

            LOWP float lightAttenuation = clamp(1.0 - (lightDistance / lightRadius), 0.0, 1.0);
            lightAttenuation *= lightAttenuation;

            LOWP vec3 radiance = lightInfo.rgb;

            if (u_lightTypes[lightId] == 0) {
                lightDirection = normalize(-u_lightDirectionsAndIntensities[lightId].xyz);
                lightDistance = length(lightDirection);
                lightAttenuation = 1.0;
            } else if (u_lightTypes[lightId] == 2) {
                float currentAngle = dot(-normalize(lightDirection), normalize(u_lightDirectionsAndIntensities[lightId].xyz));
                float innerConeAngle = cos(radians(u_lightAngles[lightId].x));
                float outerConeAngle = cos(radians(u_lightAngles[lightId].y));
                float conesAngleDiff = abs(innerConeAngle - outerConeAngle);

                float spotEffect = clamp((currentAngle - outerConeAngle) / conesAngleDiff, 0.0, 1.0);
                radiance *= spotEffect;
            }

            LOWP vec3 L = normalize(lightDirection);
            LOWP vec3 H = normalize(V + L);

            LOWP vec3 F = fresnelSchlick(max(dot(H, V), 0.0), F0);
            LOWP float NDF = distributionGGX(N, H, roughness);
            LOWP float G = geometrySmith(N, V, L, roughness);

            LOWP vec3 nominator = NDF * G * F;
            LOWP float denominator = max(4.0 * max(dot(N, V), 0.0) * max(dot(N, L), 0.0), 0.001);
            LOWP vec3 brdf = nominator / denominator;

            LOWP vec3 kS = F;
            LOWP vec3 kD = vec3(1.0) - kS;

            kD *= 1.0 - metalness;

            LOWP float NdotL = max(dot(N, L), 0.0) * u_lightDirectionsAndIntensities[lightId].w;
            Lo += (kD * albedo.rgb / M_PI + brdf) * radiance * NdotL * lightAttenuation;
        }
    #endif

    #ifdef defImageBasedLighting
        LOWP vec3 F = fresnelSchlickRoughness(max(dot(N, V), 0.0), F0, roughness);

        LOWP vec3 kS = F;
        LOWP vec3 kD = 1.0 - kS;
        kD *= 1.0 - metalness;

        LOWP vec3 irradiance = texture(u_irradiance, N).rgb;
        LOWP vec3 diffuse = irradiance * albedo.rgb;

        const float MAX_REFLECTION_LOD = 4.0;

        LOWP vec3 prefilteredColor = textureLod(u_prefilter, R, roughness * MAX_REFLECTION_LOD).rgb;
        LOWP vec2 brdf = texture(u_brdf, vec2(max(dot(N, V), 0.0), roughness)).rg;
        LOWP vec3 specular = prefilteredColor * (F * brdf.x + brdf.y);

        LOWP vec3 ambient = (kD * diffuse + specular) * ambientOcclusion;
    #else
        LOWP vec3 ambient = vec3(0.03) * albedo.rgb;
    #endif

    color = ambient + Lo;

    #ifdef defGammaCorrection
        color = color / (color + vec3(1.0));
        color = pow(color, vec3(1.0 / 2.2));
    #endif

    fragmentColor = vec4(color.rgb, albedo.a);
}