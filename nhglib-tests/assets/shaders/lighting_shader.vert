#define numBones 12

attribute vec4 a_position;
attribute vec3 a_normal;
attribute vec2 a_texCoord0;

#ifdef boneWeight0Flag
    #define boneWeightsFlag
    attribute vec2 a_boneWeight0;
#endif

#ifdef boneWeight1Flag
    attribute vec2 a_boneWeight1;
#endif

#ifdef boneWeight2Flag
    attribute vec2 a_boneWeight2;
#endif

#ifdef boneWeight3Flag
    attribute vec2 a_boneWeight3;
#endif

#ifdef boneWeight4Flag
    attribute vec2 a_boneWeight4;
#endif

#ifdef boneWeight5Flag
    attribute vec2 a_boneWeight5;
#endif

#ifdef boneWeight6Flag
    attribute vec2 a_boneWeight6;
#endif

#ifdef boneWeight7Flag
    attribute vec2 a_boneWeight7;
#endif

uniform mat4 u_modelViewProjectionMatrix;
uniform mat4 u_modelMatrix;
uniform mat4 u_viewMatrix;
uniform mat3 u_normalMatrix;
uniform vec2 u_texScale;
uniform vec2 u_texOffset;

#ifdef skinningFlag
    uniform mat4 u_bones[numBones];
#endif

varying vec3 v_position;
varying vec3 v_normal;
varying vec2 v_texCoord;

void main() {
    #ifdef skinningFlag
        mat4 skinning = mat4(0.0);

        #ifdef boneWeight0Flag
            skinning += (a_boneWeight0.y) * u_bones[int(a_boneWeight0.x)];
        #endif

        #ifdef boneWeight1Flag
            skinning += (a_boneWeight1.y) * u_bones[int(a_boneWeight1.x)];
        #endif

        #ifdef boneWeight2Flag
            skinning += (a_boneWeight2.y) * u_bones[int(a_boneWeight2.x)];
        #endif

        #ifdef boneWeight3Flag
            skinning += (a_boneWeight3.y) * u_bones[int(a_boneWeight3.x)];
        #endif

        #ifdef boneWeight4Flag
            skinning += (a_boneWeight4.y) * u_bones[int(a_boneWeight4.x)];
        #endif

        #ifdef boneWeight5Flag
            skinning += (a_boneWeight5.y) * u_bones[int(a_boneWeight5.x)];
        #endif

        #ifdef boneWeight6Flag
            skinning += (a_boneWeight6.y) * u_bones[int(a_boneWeight6.x)];
        #endif

        #ifdef boneWeight7Flag
            skinning += (a_boneWeight7.y) * u_bones[int(a_boneWeight7.x)];
        #endif
    #endif

    vec4 pos = vec4(1.0);
    vec3 nor = vec3(1.0);

    #ifdef skinningFlag
        pos = u_modelMatrix * skinning * a_position;
        nor = vec3(normalize(u_modelMatrix * skinning * vec4(a_normal, 0.0)));
    #else
        pos = u_modelMatrix * a_position;
        nor = normalize(u_normalMatrix * a_normal);
    #endif

    gl_Position = u_modelViewProjectionMatrix * pos;

    v_position = vec3(pos);
    v_normal = nor;
	v_texCoord.x = a_texCoord0.x * u_texScale.x + u_texOffset.x;
	v_texCoord.y = a_texCoord0.y * u_texScale.y + u_texOffset.y;
}