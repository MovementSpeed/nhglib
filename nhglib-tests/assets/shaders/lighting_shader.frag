#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

#ifdef ambientLighting
    uniform vec3 u_ambientLightColor;
#endif

#ifdef numDirectionalLights
    #if numDirectionalLights > 0
        struct DirectionalLight {
            vec3 color;
            vec3 direction;
            float intensity;
        };

        uniform DirectionalLight u_dirLights[numDirectionalLights];
    #endif
#endif

#ifdef numPointLights
    #if numPointLights > 0
        struct PointLight {
            vec3 color;
            vec3 position;
            float intensity;
            float radius;
        };

        uniform PointLight u_pointLights[numPointLights];
    #endif
#endif

#ifdef numSpotLights
    #if numSpotLights > 0
        struct SpotLight {
            vec3 color;
            vec3 position;
            vec3 direction;
            float intensity;
            float cutoffAngle;
            float exponent;
        };

        uniform SpotLight u_spotLights[numSpotLights];
    #endif
#endif

uniform sampler2D u_diffuse;

varying vec3 v_position;
varying vec3 v_normal;
varying vec2 v_texCoord;

const float outerConeAngle = 0.93; // 20 degrees cos(20 deg)

vec3 computeLighting() {
    vec3 lighting = vec3(0.0);

    // Ambient light color
    #ifdef ambientLighting
        lighting.rgb += u_ambientLightColor;
    #endif

    // Directional lights
    #ifdef numDirectionalLights
        #if numDirectionalLights > 0
            for (int i = 0; i < numDirectionalLights; i++) {
                vec3 lightDir = -u_dirLights[i].direction;
                float NdotL = max(dot(v_normal, lightDir), 0.0);
                lighting.rgb += u_dirLights[i].color * NdotL * u_dirLights[i].intensity;
            }
        #endif
    #endif

    // Point lights
    #ifdef numPointLights
        #if numPointLights > 0
            for (int i = 0; i < numPointLights; i++) {
                vec3 lightDir = normalize(u_pointLights[i].position - v_position);

                float diff = max(dot(v_normal, lightDir), 0.0);
                float distance = length(u_pointLights[i].position - v_position);
                float attenuation = clamp(1.0 - distance / u_pointLights[i].radius, 0.0, 1.0);
                attenuation *= attenuation;

                lighting.rgb += (u_pointLights[i].color * diff * u_pointLights[i].intensity) * attenuation;
            }
        #endif
    #endif

    // Spot lights
    #ifdef numSpotLights
        #if numSpotLights > 0
            for (int i = 0; i < numPointLights; i++) {
                vec3 lightDir = u_spotLights[i].position - v_position;

                float currentAngle = dot(-normalize(lightDir), normalize(u_spotLights[i].direction));
                float innerConeAngle = cos(radians(u_spotLights[i].cutoffAngle));
                float conesAngleDiff = abs(innerConeAngle - outerConeAngle);

                float spotEffect = clamp((currentAngle - outerConeAngle) / conesAngleDiff, 0.0, 1.0);

                float dist2 = dot(lightDir, lightDir);
                lightDir *= inversesqrt(dist2);

                float NdotL = max(dot(v_normal, lightDir), 0.0);
                float falloff = max(u_spotLights[i].intensity / (1.0 + dist2), 2.0);

                float distance = length(u_spotLights[i].position - v_position);
                float attenuation = clamp(1.0 - distance / u_spotLights[i].intensity, 0.0, 1.0);
                attenuation *= attenuation;

                lighting.rgb += ((u_spotLights[i].color * (NdotL * falloff) * spotEffect) * u_spotLights[i].intensity) * attenuation;
            }
        #endif
    #endif

    return lighting;
}

void main() {
    vec3 lighting = computeLighting();
	gl_FragColor = vec4(lighting, 0.0) * texture2D(u_diffuse, v_texCoord);
}