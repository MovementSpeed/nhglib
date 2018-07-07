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

#ifdef billboard
//Billboard particles
IN vec4 v_color;
IN MED vec2 v_texCoords0;
uniform sampler2D u_diffuseTexture;

void main() {
	FRAG_COLOR = TEXTURE(u_diffuseTexture, v_texCoords0) * v_color;
}
#else

//Point particles
IN float v_depth;
IN vec2 v_uvRegionCenter;
IN vec4 v_color;
IN vec4 v_rotation;
IN MED vec4 v_region;

uniform float u_softness;
uniform vec2 u_screen;
uniform vec2 u_cameraRange;
uniform sampler2D u_diffuseTexture;
uniform sampler2D u_depthTexture;

float contrast(float d, float softness) {
    float val = clamp(2.0 * ((d > 0.5) ? 1.0 - d : d), 0.0, 1.0);
    float a = 0.5 * pow(val, softness);

    return (d > 0.5) ? 1.0 - a : a;
}

void main()
{
	vec2 uv = v_region.xy + gl_PointCoord * v_region.zw - v_uvRegionCenter;
	vec2 texCoord = mat2(v_rotation.x, v_rotation.y, v_rotation.z, v_rotation.w) * uv + v_uvRegionCenter;
	vec4 color = TEXTURE(u_diffuseTexture, texCoord) * v_color;

    #ifdef SOFT_PARTICLES
	vec2 dcoords = vec2(gl_FragCoord.x / u_screen.x, gl_FragCoord.y / u_screen.y);
	vec4 depthMap = TEXTURE(u_depthTexture, dcoords);

	float depth = depthMap.x;
	float pdepth = (v_depth - u_cameraRange.x) / (u_cameraRange.y - u_cameraRange.x);
    gl_FragDepth = pdepth;

	float zdiff = depth - pdepth;

	HIGH float weight = contrast(zdiff, u_softness);
	color.a = color.a * weight;
	#endif

	FRAG_COLOR = color;
}

#endif