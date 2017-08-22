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

in vec3 v_localPos;

uniform sampler2D u_equirectangularMap;

const vec2 invAtan = vec2(0.1591, 0.3183);

vec2 SampleSphericalMap(vec3 v)
{
    vec2 uv = vec2(atan(v.z, v.x), asin(v.y));
    uv *= invAtan;
    uv += 0.5;
    return uv;
}

void main()
{
    vec2 uv = SampleSphericalMap(normalize(v_localPos)); // make sure to normalize localPos
    vec3 color = texture(u_equirectangularMap, vec2(uv.x, 1.0 - uv.y)).rgb;

    fragmentColor = vec4(color, 1.0);
}