varying vec3 v_localPos;
varying vec2 v_texCoord;

uniform samplerCube u_environmentMap;

void main()
{
    vec3 envColor = textureCube(u_environmentMap, v_localPos).rgb;

    envColor = envColor / (envColor + vec3(1.0));
    envColor = pow(envColor, vec3(1.0/2.2));

    gl_FragColor = vec4(envColor, 1.0);
}