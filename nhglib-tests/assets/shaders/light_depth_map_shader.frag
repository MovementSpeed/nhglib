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

IN HIGHP vec4 v_position;

uniform HIGHP float u_cameraFar;
uniform HIGHP vec3 u_lightPosition;

void main() {
	// Simple depth calculation, just the length of the vector light-current position
	// TODO : questa funziona con le luci direzionali
	//FRAG_COLOR = vec4(vec3(gl_FragCoord.z), 1.0);

	// TODO : questa funziona con le luci omnidirezionali
	float lightDistance = length(v_position.xyz - u_lightPosition);
	FRAG_COLOR = vec4(vec3(lightDistance), 1.0);
}