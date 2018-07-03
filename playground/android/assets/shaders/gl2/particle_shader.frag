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

#ifdef billboard
//Billboard particles
varying vec4 v_color;
varying MED vec2 v_texCoords0;
uniform sampler2D u_diffuseTexture;

void main() {
	gl_FragColor = texture(u_diffuseTexture, v_texCoords0) * v_color;
}
#else

//Point particles
varying float v_depth;
varying vec2 v_uvRegionCenter;
varying vec4 v_color;
varying vec4 v_rotation;
varying MED vec4 v_region;

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
	vec4 color = texture2D(u_diffuseTexture, texCoord) * v_color;

    #ifdef SOFT_PARTICLES
        vec2 dcoords = vec2(gl_FragCoord.x / u_screen.x, gl_FragCoord.y / u_screen.y);
        vec4 depthMap = texture2D(u_depthTexture, dcoords);

        float depth = depthMap.x;
        float pdepth = (v_depth - u_cameraRange.x) / (u_cameraRange.y - u_cameraRange.x);
        gl_FragDepth = pdepth;

        float zdiff = depth - pdepth;

        HIGH float weight = contrast(zdiff, u_softness);
        color.a = color.a * weight;
	#endif

	gl_FragColor = color;
}

#endif