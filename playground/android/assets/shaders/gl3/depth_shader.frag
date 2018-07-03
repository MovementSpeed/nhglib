#version 300 es

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

out vec4 fragmentColor;

in vec4 v_position;
in float v_depth;

uniform vec2 u_cameraRange;

void main()
{
	float z = (v_depth - u_cameraRange.x) / (u_cameraRange.y - u_cameraRange.x);
	gl_FragDepth = z;
	fragmentColor = vec4(vec3(z), 1.0);
}
