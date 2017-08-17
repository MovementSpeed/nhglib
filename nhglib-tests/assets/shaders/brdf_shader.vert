attribute vec3 a_position;
attribute vec2 a_texCoord0;

varying vec2 v_texCoord;

void main()
{
    v_texCoord = a_texCoord0;
	gl_Position = vec4(a_position, 1.0);
}