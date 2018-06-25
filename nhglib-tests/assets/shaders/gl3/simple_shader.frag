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

#ifdef defAlbedo
    uniform LOWP sampler2D u_albedo;
#endif

in LOWP vec2 v_texCoord;

out vec4 resultColor;

void main() {
    #ifdef defAlbedo
        LOWP vec3 albedo = texture(u_albedo, v_texCoord).rgb;
    #else
        LOWP vec3 albedo = vec3(0.5);
    #endif

	resultColor = vec4(albedo, 1.0);
}