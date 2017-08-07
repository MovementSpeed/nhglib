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

#if defined(diffuseTextureFlag) && defined(blendedFlag)
#define blendedTextureFlag
varying MED vec2 v_texCoords0;
uniform sampler2D u_diffuseTexture;
uniform float u_alphaTest;
#endif

//#ifdef PackedDepthFlag
varying HIGH float v_depth;
//#endif //PackedDepthFlag

//varying vec4 vpos;

uniform mat4 u_projViewWorldTrans;

void main() {
	/*#ifdef blendedTextureFlag
		if (texture2D(u_diffuseTexture, v_texCoords0).a < u_alphaTest)
			discard;
	#endif // blendedTextureFlag*/

	//#ifdef PackedDepthFlag
		//gl_FragColor = vec4(vec3(v_depth), 1.0);
	//#endif //PackedDepthFlag

	/*float A = gl_ProjectionMatrix[2].z;
    float B = gl_ProjectionMatrix[3].z;*/
    float zNear = 0.01;
    float zFar = 100.0;

    float depthFF = gl_FragCoord.z;

    gl_FragColor = vec4(vec3(depthFF), 1.0);
}