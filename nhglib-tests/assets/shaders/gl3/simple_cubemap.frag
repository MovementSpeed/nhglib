varying vec3 v_localPos;
varying vec2 v_texCoord;

//uniform sampler2D u_environmentMap;
uniform samplerCube u_environmentMap;

void main()
{
    vec3 envColor = textureCube(u_environmentMap, v_localPos).rgb;
    //vec3 envColor = texture2D(u_environmentMap, v_texCoord).rgb;

    envColor = envColor / (envColor + vec3(1.0));
    envColor = pow(envColor, vec3(1.0/2.2));

    gl_FragColor = vec4(envColor, 1.0);
}