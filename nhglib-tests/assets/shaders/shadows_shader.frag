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
uniform sampler2D u_depthMapDir;
uniform samplerCube u_depthMapCube;

IN HIGHP vec4 v_position;
IN HIGHP vec3 v_normal;
IN HIGHP vec4 v_fragPosLightSpace;

void main() {
    // perform perspective divide
    vec3 projCoords = v_fragPosLightSpace.xyz / v_fragPosLightSpace.w;

    // transform to [0,1] range
    projCoords = projCoords * 0.5 + 0.5;

    vec3 lightDir = normalize(u_lightPosition - v_position.xyz);

    float closestDepth;

    // get closest depth value from light's perspective (using [0,1] range fragPosLight as coords)
    if (u_type == 1.0) {
        closestDepth = TEXTURE(u_depthMapDir, projCoords.xy).r;
    } else if (u_type == 2.0) {
        closestDepth = TEXTURE_CUBE(u_depthMapCube, lightDir).r;
    }

    // get depth of current fragment from light's perspective
    float currentDepth = projCoords.z;

    vec3 normal = normalize(v_normal);

    // check whether current frag pos is in shadow
    float bias = max(0.0026 * (1.0 - dot(normal, lightDir)), 0.0026);

    float shadow = 0.0;
    vec2 texelSize = vec2(1.0 / vec2(1024.0, 1024.0));
    for(int x = -1; x <= 1; ++x)
    {
        for(int y = -1; y <= 1; ++y)
        {
            float pcfDepth = TEXTURE(u_depthMapDir, projCoords.xy + vec2(float(x), float(y)) * texelSize).r;
            shadow += currentDepth - bias > pcfDepth  ? 1.0 : 0.0;
        }
    }
    shadow /= 9.0;

    // keep the shadow at 0.0 when outside the far_plane region of the light's frustum.
    if(projCoords.z > 1.0)
        shadow = 0.0;

    FRAG_COLOR = vec4(shadow);
}

