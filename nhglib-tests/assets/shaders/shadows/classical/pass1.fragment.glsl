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
    #define IN varying
    #define FRAG_COLOR gl_FragColor
#else
    #define TEXTURE texture
    #define IN in
    #define FRAG_COLOR fragmentColor
    out vec4 fragmentColor;
#endif

/**
 Pack a float into a vec4
 It allows to save the float in the texture with a 32 bits precision
*/
vec4 pack(HIGHP float depth) {
	const vec4 bitSh = vec4(256 * 256 * 256, 256 * 256, 256, 1.0);
	const vec4 bitMsk = vec4(0, 1.0 / 256.0, 1.0 / 256.0, 1.0 / 256.0);
	vec4 comp = fract(depth * bitSh);
	comp -= comp.xxyz * bitMsk;
	return comp;
}

void main()
{
	FRAG_COLOR = pack(gl_FragCoord.z);
}
