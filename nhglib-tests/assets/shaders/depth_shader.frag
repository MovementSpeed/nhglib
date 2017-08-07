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

varying vec4 v_position;
varying float v_depth;

void main()
{
	float z = (v_depth - 0.01) / (2.0 - 0.01);
	gl_FragDepth = z;
	gl_FragColor = vec4(vec3(z), 1.0);
}
