#ifdef GL_ES
    #ifdef GPU_MALI
        #define LOWP lowp
        #define MEDP mediump
        #define HIGHP highp
    #elif defined GPU_ADRENO
        #define LOWP
        #define MEDP
        #define HIGHP
    #else
        #define MEDP
        #define LOWP
        #define HIGHP
    #endif

    precision mediump float;
#else
    #define MEDP
    #define LOWP
    #define HIGHP
#endif

uniform sampler2D u_depthTex;
uniform mat4 u_ipMatrix;

out vec4 resultMinMax;

//converts clip space depth to view space
float convertToSS(float depth)
{
	vec4 pt = u_ipMatrix * vec4(0.0, 0.0, 2.0 * depth - 1.0, 1.0);
	return pt.z/pt.w;
}

void main()
{
	ivec2 resolution = ivec2(WIDTH, HEIGHT);

	vec2 minMax = vec2(1.0f, -1.0f);

	//calc offset and range of tile
	ivec2 offset = ivec2(gl_FragCoord.xy) * ivec2(TILE_DIM, TILE_DIM);
	ivec2 end = min(resolution, offset + ivec2(TILE_DIM, TILE_DIM));

	for (int y = offset.y; y < end.y; y++)
	{
		for (int x = offset.x; x < end.x; x++)
		{
			float d = texelFetch(depthTex, ivec2(x, y), 0).x;

			if (d < 1.0)
			{
				minMax.x = min(minMax.x, d);
				minMax.y = max(minMax.y, d);
			}
		}
	}

	vec2 result = minMax;

	minMax = vec2(convertToSS(minMax.x), convertToSS(minMax.y));
	resultMinMax = vec4(minMax,result);
}
