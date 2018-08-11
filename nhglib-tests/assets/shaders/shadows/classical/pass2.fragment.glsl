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

#if GLVERSION == 2
    #define TEXTURE texture2D
    #define IN varying
    #define FRAG_COLOR gl_FragColor
#else
    #define TEXTURE texture
    #define IN in
    #define FRAG_COLOR fragmentColor
    out vec4 fragmentColor;
#endif

uniform LOWP vec3 u_lightColor;
uniform HIGHP vec3 u_lightDirection;

#ifdef spotLight
	uniform HIGHP vec3 u_lightPosition;
	uniform LOWP float u_lightIntensity;
	uniform LOWP float u_lightCutoffAngle;
	uniform LOWP float u_lightExponent;
#endif

IN HIGHP vec3 v_position;

#if defined(normalFlag)
	IN HIGHP vec3 v_normal;
#endif // normalFlag

IN HIGHP vec4 v_shadowMapUv;

const float u_shininess = 10.0;

uniform LOWP sampler2D u_shadowTexture;
uniform HIGHP vec4 u_uvTransform;

float unpack (vec4 colour) {
	const vec4 bitShifts = vec4(1.0 / (256.0 * 256.0 * 256.0),
								1.0 / (256.0 * 256.0),
								1.0 / 256.0,
								1);
	return dot(colour , bitShifts);
}

void main()
{
	const float bias = 0.0026;
	vec3 depth = (v_shadowMapUv.xyz / v_shadowMapUv.w)*0.5+0.5;
	vec2 uv = u_uvTransform.xy + depth.xy * u_uvTransform.zw;
	float lenDepthMap = unpack(TEXTURE(u_shadowTexture, uv));
	float shadow = 0.0;

	#ifdef directionalLight
		if (depth.x >= 0.0 &&
			depth.x <= 1.0 &&
			depth.y >= 0.0 &&
			depth.y <= 1.0
		) {
		    shadow += depth.z - bias > lenDepthMap ? 1.0 : 0.0;
			//if( depth.z - lenDepthMap > bias ) {
				//vec3 lightDir = -u_lightDirection;
				// Diffuse
				//float NdotL = clamp(dot(normal, lightDir), 0.0, 1.0);
				//lightDiffuse.rgb += 1.0;
			//}
		}

	#endif

	// Spot Lights
	#ifdef spotLight
		if (depth.x >= 0.0 &&
			depth.x <= 1.0 &&
			depth.y >= 0.0 &&
			depth.y <= 1.0 //&&
			//v_shadowMapUv.z >= 0.0
		) {
			if (depth.z - bias > lenDepthMap) {
			    shadow += 1.0;
				/*vec3 lightDir = u_lightPosition - v_position;

				float spotEffect = dot(-normalize(lightDir), normalize(u_lightDirection));
				if ( spotEffect  > cos(radians(u_lightCutoffAngle)) ) {
					spotEffect = max( pow( max( spotEffect, 0.0 ), u_lightExponent ), 0.0 );
					float dist2 = dot(lightDir, lightDir);
					lightDir *= inversesqrt(dist2);
					float NdotL = clamp(dot(normal, lightDir), 0.0, 2.0);
					float falloff = clamp(u_lightIntensity / (1.0 + dist2), 0.0, 2.0);

					// Diffuse
					lightDiffuse += u_lightColor * (NdotL * falloff) * spotEffect;
				}*/
			}
		}
	#endif

	FRAG_COLOR = vec4(shadow);
}
