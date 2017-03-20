#ifdef GL_ES
precision mediump float;
#endif

uniform int u_graphicsWidth;
uniform int u_graphicsHeight;

#ifdef lights
    #if lights > 0
        struct Light {
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

#ifdef diffuse
    uniform sampler2D u_diffuse;
#endif

#ifdef specular
    uniform sampler2D u_specular;
#endif

#ifdef normal
    uniform sampler2D u_normal;

    varying vec3 v_binormal;
    varying vec3 v_tangent;
#endif

varying vec2 v_texCoord;
varying vec3 v_position;
varying vec3 v_normal;

void main() {
    vec3 viewDir = normalize(-v_position);
    vec4 contribution = vec4(1.0, 1.0, 1.0, 0.0);

    #ifdef diffuse
        vec4 color = texture2D(u_diffuse, v_texCoord);
    #else
        vec4 color = vec4(1.0);
    #endif

    #ifdef lights
        int tileX = int(gl_FragCoord.x) / (u_graphicsWidth / 10);
        int tileY = int(gl_FragCoord.y) / (u_graphicsHeight / 10);

        float textureRow = float(tileY * 10.0 + tileX) / 128.0;

        vec4 pixel = texture2D(u_lights, vec2(0.5 / 64.0, textureRow));
        int pixelCeil = int(ceil(pixel.r * 255.0));

        for (int i = 0; i < pixelCeil; i++) {
            vec4 tempPixel = texture2D(u_lights, vec2((float(i) + 1.5) / 64.0, textureRow));
            int lightId = int(clamp(ceil(tempPixel.r * 255.0), 0.0, 255.0));

            vec4 lightInfo = texture2D(u_lightInfo, vec2(0.5, float(lightId) / 128.0));

            float lightRadius = lightInfo.a * 255.0;
            lightInfo.a = 1.0;

            vec3 lightDirection = u_lightsList[lightId].position - v_position;
            float lightDistance = length(lightDirection);

            vec3 N = normalize(v_normal);
            vec3 L = normalize(lightDirection);

            #ifdef normal
                vec3 tangent = normalize(v_tangent);
                vec3 bitangent = cross(tangent, normal);
                vec3 normal = normalize(v_normal);

                tangent = normalize(tangent - dot(tangent, normal) * normal);
                mat3 tbn = mat3(tangent, bitangent, normal);

                vec3 pn = normalize(tbn * (texture2D(u_normal, v_texCoord).xyz * 2.0 - 1.0));
            #else
                vec3 pn = N;
            #endif

            float NdotDir = dot(pn, L);

            // apply lighting
            float lightAttenuation = clamp(1.0 - lightDistance / lightRadius, 0.0, 1.0);
            lightAttenuation *= lightAttenuation;

            float diffuseTerm =
                    lightAttenuation *
                    clamp(NdotDir, 0.0, 1.0) *
                    clamp((lightRadius - lightDistance), 0.0, 1.0);

            float specularTerm = 0.0;

            #ifdef specular
                float specular = 0.0;
                float shininess = texture2D(u_specular, v_texCoord.st).a;
                float lambertian = max(NdotDir, 0.0);

                if (lambertian > 0.0) {
                    vec3 R = reflect(-L, pn);
                    vec3 V = normalize(viewDir);

                    float specularAngle = max(dot(R, V), 0.0);
                    specular = pow(specularAngle, shininess * 255.0);
                }

                specularTerm = (lightAttenuation * 16.0 * specular) * shininess;
            #endif

            contribution += (diffuseTerm + specularTerm) * lightInfo;
        }
    #endif

    gl_FragColor = vec4(color * contribution);
}
