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
	gl_FragColor = texture2D(u_diffuseTexture, v_texCoords0) * v_color;
}
#else

//Point particles
varying float v_depth;
varying vec2 v_uvRegionCenter;
varying vec4 v_color;
varying vec4 v_rotation;
varying MED vec4 v_region;

uniform sampler2D u_diffuseTexture;
uniform sampler2D u_depthTexture;

float contrast(float d, float softness) {
    float val = clamp(2.0 * ((d > 0.5) ? 1.0 - d : d), 0.0, 1.0);
    float a = 0.5 * pow(val, softness);

    return (d > 0.5) ? 1.0 - a : a;
}

void main() {
	vec2 uv = v_region.xy + gl_PointCoord * v_region.zw - v_uvRegionCenter;
	vec2 texCoord = mat2(v_rotation.x, v_rotation.y, v_rotation.z, v_rotation.w) * uv + v_uvRegionCenter;

	vec2 dcoords = vec2(gl_FragCoord.x / 1280.0, gl_FragCoord.y / 720.0);
	vec4 depthMap = texture2D(u_depthTexture, dcoords);

	float depth = depthMap.x;
	float pdepth = (v_depth - 0.01) / (2.0 - 0.01);
    gl_FragDepth = pdepth;

	float zdiff = depth - pdepth;

    /*if (zdiff <= 0.0) {
        discard;
    }*/

    //vec4 color = depthMap;
	vec4 color = texture2D(u_diffuseTexture, texCoord) * v_color;
	//vec4 color = vec4(1.0);
	//vec4 color = vec4(vec3(pdepth), 1.0);

	HIGH float weight = contrast(zdiff, 1.0);
	color.a = color.a * weight;

	//color.a = 0.1;

	//vec4 color = vec4(vec3(zdiff), 1.0);

	/*if (pdepth == depth) {
	    discard;
	}*/

    /*if (zdiff <= 0.0){
        discard;
    }*/

    //vec4 color = vec4(vec3(1.0, 1.0, 1.0), z);

	//vec4 color = vec4(vec3(pdepth), 1.0);

	gl_FragColor = color;
}

#endif