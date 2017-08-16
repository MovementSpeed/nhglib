attribute vec3 a_position;

varying vec3 v_localPos;

uniform mat4 u_projection;
uniform mat4 u_view;

void main()
{
    v_localPos = a_position;
    gl_Position =  u_projection * u_view * vec4(v_localPos, 1.0);
}