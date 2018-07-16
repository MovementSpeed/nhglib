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

#if GLVERSION == 2
    #define TEXTURE texture2D
    #define TEXTURE_CUBE textureCube
    #define IN varying
    #define FRAG_COLOR gl_FragColor
#else
    #define TEXTURE texture
    #define TEXTURE_CUBE texture
    #define IN in
    #define FRAG_COLOR fragmentColor
    out vec4 fragmentColor;
#endif

uniform float u_type;
uniform float u_cameraFar;
uniform vec3 u_lightPosition;
uniform vec3 u_cameraPosition;
uniform sampler2D u_depthMapDir;
uniform samplerCube u_depthMapCube;

IN HIGHP vec4 v_position;
IN HIGHP vec3 v_normal;
IN HIGHP vec4 v_fragPosLightSpace;

vec3 sampleOffsetDirections[20] = vec3[]
(
   vec3( 1,  1,  1), vec3( 1, -1,  1), vec3(-1, -1,  1), vec3(-1,  1,  1),
   vec3( 1,  1, -1), vec3( 1, -1, -1), vec3(-1, -1, -1), vec3(-1,  1, -1),
   vec3( 1,  1,  0), vec3( 1, -1,  0), vec3(-1, -1,  0), vec3(-1,  1,  0),
   vec3( 1,  0,  1), vec3(-1,  0,  1), vec3( 1,  0, -1), vec3(-1,  0, -1),
   vec3( 0,  1,  1), vec3( 0, -1,  1), vec3( 0, -1, -1), vec3( 0,  1, -1)
);

void main() {
    // perform perspective divide
    vec3 projCoords = v_fragPosLightSpace.xyz / v_fragPosLightSpace.w;

    // transform to [0,1] range
    projCoords = projCoords * 0.5 + 0.5;

    vec3 normal = normalize(v_normal);
    vec3 lightDir = v_position.xyz - u_lightPosition;
    float bias = max(0.0026 * (1.0 - dot(normal, lightDir)), 0.0026);

    float shadow = 0.0;

    // get closest depth value from light's perspective (using [0,1] range fragPosLight as coords)
    if (u_type == 1.0) {
        vec2 texelSize = vec2(1.0 / vec2(1024.0, 1024.0));
        float currentDepth = projCoords.z;

        for(int x = -1; x <= 1; ++x) {
            for(int y = -1; y <= 1; ++y) {
                float pcfDepth = TEXTURE(u_depthMapDir, projCoords.xy + vec2(x, y) * texelSize).r;
                shadow += currentDepth - bias > pcfDepth  ? LIGHT_CONTRIBUTE : 0.0;
            }
        }

        shadow /= 9.0;

        // keep the shadow at 0.0 when outside the far_plane region of the light's frustum.
        if(projCoords.z > 1.0) {
            shadow = 0.0;
        }
    } else if (u_type == 2.0) {
        int samples = 20;
        float diskRadius = 0.001;
        float currentDepth = length(lightDir);
        for(int i = 0; i < samples; ++i)
        {
            float closestDepth = TEXTURE_CUBE(u_depthMapCube, lightDir + sampleOffsetDirections[i] * diskRadius).r;
            if(currentDepth - bias > closestDepth)
                shadow += LIGHT_CONTRIBUTE;
        }
        shadow /= float(samples);
    }

    FRAG_COLOR = vec4(shadow);
}

