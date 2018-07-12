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
IN HIGHP vec4 v_positionLightMatrix;

void main() {
	// Default is to not add any color
	float intensity = 0.0;

	// Vector light-current position
	vec3 lightDirection = v_position.xyz - u_lightPosition;
	float lenToLight = length(lightDirection) / u_cameraFar;

	// By default assume shadow
	float lenDepthMap = -1.0;

	if (u_type == 1.0) {
	    vec3 depth = (v_positionLightMatrix.xyz / v_positionLightMatrix.w) * 0.5 + 0.5;
        if (v_positionLightMatrix.z >= 0.0 && (depth.x >= 0.0) && (depth.x <= 1.0) && (depth.y >= 0.0) && (depth.y <= 1.0)) {
            lenDepthMap = TEXTURE(u_depthMapDir, depth.xy).r;
        }
	} else if (u_type == 2.0) {
	    // Point light, just get the depth given light vector
        lenDepthMap = TEXTURE_CUBE(u_depthMapCube, lightDirection).r;
	}

	// If not in shadow, add some light
	if (lenDepthMap < lenToLight - 0.005) {
	} else {
		intensity = 0.5 * (1.0 - lenToLight);
	}

	FRAG_COLOR = vec4(intensity);
}

