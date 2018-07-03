attribute vec3 a_position;
attribute vec2 a_texCoord0;

uniform mat4 u_projection;
uniform mat4 u_view;

varying vec3 v_localPos;
varying vec2 v_texCoord;

void main()
{
    v_localPos = a_position;
    v_texCoord = a_texCoord0;

    mat4 rotView = mat4(mat3(u_view)); // remove translation from the view matrix
    vec4 clipPos = u_projection * rotView * vec4(v_localPos, 1.0);

    gl_Position = clipPos.xyww;
}