#version 300 es
layout (location = 0) in vec3 a_position;
layout (location = 1) in vec2 a_texCoord0;

out vec2 TexCoords;

void main()
{
    TexCoords = a_texCoord0;
    gl_Position = vec4(a_position, 1.0);
}