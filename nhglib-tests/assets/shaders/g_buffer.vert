#version 300 es
layout (location = 0) in vec3 a_position;
layout (location = 1) in vec3 a_normal;
layout (location = 2) in vec2 a_texCoord0;

out vec3 FragPos;
out vec2 TexCoords;
out vec3 Normal;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main()
{
    vec4 worldPos = model * vec4(a_position, 1.0);
    FragPos = worldPos.xyz;
    TexCoords = a_texCoord0;

    mat3 normalMatrix = transpose(inverse(mat3(model)));
    Normal = normalMatrix * a_normal;

    gl_Position = projection * view * worldPos;
}