#ifdef GL_ES
#define LOWP lowp
#define MED mediump
#define HIGH highp
precision mediump float;
#else
#define MED
#define LOWP
#define HIGH
#endif

uniform float u_cameraFar;
uniform vec3 u_lightPosition;

varying vec4 v_position;
varying float v_depth;

void main()
{
	// Simple depth calculation, just the length of the vector light-current position
	//gl_FragColor = vec4(vec3(length(v_position) / (4.0 - 0.01)), 1.0);

	float z = (v_depth - 0.01) / (10.0 - 0.01);
	gl_FragDepth = z;
	gl_FragColor = vec4(vec3(z), 1.0);
}
