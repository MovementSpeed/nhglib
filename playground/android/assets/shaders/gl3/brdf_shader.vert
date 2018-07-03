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

in vec3 a_position;
in vec2 a_texCoord0;

out vec2 v_texCoord;

void main()
{
    v_texCoord = a_texCoord0;
	gl_Position = vec4(a_position, 1.0);
}