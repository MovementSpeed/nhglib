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
//In
in vec3 a_position;
in vec2 a_texCoord0;
in vec4 a_sizeAndRotation;
in vec4 a_color;

//out
out MED vec2 v_texCoords0;
out vec4 v_color;

//Camera
uniform mat4 u_projViewTrans;

//Billboard to screen
#ifdef screenFacing
uniform vec3 u_cameraInvDirection;
uniform vec3 u_cameraRight;
uniform vec3 u_cameraUp;
#endif
#ifdef viewPointFacing
uniform vec3 u_cameraPosition;
uniform vec3 u_cameraUp;
#endif
#ifdef paticleDirectionFacing
uniform vec3 u_cameraPosition;
in vec3 a_direction;
#endif

void main()
{
#ifdef screenFacing
	vec3 right = u_cameraRight;
	vec3 up = u_cameraUp;
	vec3 look = u_cameraInvDirection;
#endif
#ifdef viewPointFacing
	vec3 look = normalize(u_cameraPosition - a_position);
	vec3 right = normalize(cross(u_cameraUp, look));
	vec3 up = normalize(cross(look, right));
#endif
#ifdef paticleDirectionFacing
	vec3 up = a_direction;
	vec3 look = normalize(u_cameraPosition - a_position);
	vec3 right = normalize(cross(up, look));
	look = normalize(cross(right, up));
#endif

	//Rotate around look
	vec3 axis = look;
	float c = a_sizeAndRotation.z;
    float s = a_sizeAndRotation.w;
    float oc = 1.0 - c;

    mat3 rot = mat3(oc * axis.x * axis.x + c, oc * axis.x * axis.y - axis.z * s,  oc * axis.z * axis.x + axis.y * s,
                oc * axis.x * axis.y + axis.z * s,  oc * axis.y * axis.y + c,           oc * axis.y * axis.z - axis.x * s,
                oc * axis.z * axis.x - axis.y * s,  oc * axis.y * axis.z + axis.x * s,  oc * axis.z * axis.z + c);
	vec3 offset = rot*(right*a_sizeAndRotation.x + up*a_sizeAndRotation.y );

	gl_Position = u_projViewTrans * vec4(a_position + offset, 1.0);
	v_texCoords0 = a_texCoord0;
	v_color = a_color;
}
#else
//Point particles
in vec3 a_sizeAndRotation;
in vec4 a_position;
in vec4 a_color;
in vec4 a_region;

//out
out float v_depth;
out vec2 v_uvRegionCenter;
out vec4 v_position;
out vec4 v_color;
out vec4 v_rotation;
out MED vec4 v_region;

uniform vec2 u_screen;
uniform vec2 u_regionSize;
uniform mat4 u_modelMatrix;
uniform mat4 u_viewMatrix;
uniform mat4 u_projectionMatrix;

//Camera
//uniform mat4 u_projTrans;
//should be modelView but particles are already in world coordinates
//uniform mat4 u_viewTrans;

void main()
{
	float halfSize = a_sizeAndRotation.x * 0.5;

	vec4 eyePos = u_viewMatrix * a_position;
	vec4 projCorner = u_projectionMatrix * vec4(halfSize, halfSize, eyePos.z, eyePos.w);

	gl_PointSize = u_screen.x * projCorner.x / projCorner.w;

	v_position = eyePos;

	vec4 fpos = u_projectionMatrix * v_position;
	v_depth = fpos.z;
	gl_Position = fpos;

	v_rotation = vec4(a_sizeAndRotation.y, a_sizeAndRotation.z, -a_sizeAndRotation.z, a_sizeAndRotation.y);
	v_color = a_color;

	v_region.xy = a_region.xy;
	v_region.zw = a_region.zw - a_region.xy;

	v_uvRegionCenter = a_region.xy + v_region.zw * 0.5;
}

#endif