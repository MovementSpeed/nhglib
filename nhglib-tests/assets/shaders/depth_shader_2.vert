attribute vec4 a_position;
attribute vec3 a_normal;
attribute vec3 a_binormal;
attribute vec3 a_tangent;
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

#if defined(numBones) && defined(boneWeightsFlag)
    #if (numBones > 0)
        #define skinningFlag
    #endif
#endif

#ifdef numBones
    #if numBones > 0
        uniform mat4 u_bones[numBones];
    #endif
#endif

uniform mat4 u_mvpMatrix;
uniform mat4 u_modelMatrix;
uniform mat4 u_viewMatrix;

varying vec3 v_position;
varying vec2 v_texCoord;
varying vec3 v_normal;
varying vec3 v_binormal;
varying vec3 v_tangent;
varying float v_depth;

void main() {
    vec4 position;
    vec3 normal;

    #ifdef numBones
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
    #else
        mat4 skinning = mat4(1.0);
    #endif

    position = u_modelMatrix * skinning * a_position;
    normal = normalize(vec3(u_viewMatrix * u_modelMatrix * skinning * vec4(a_normal, 0.0)));

	v_position = vec3(u_viewMatrix * position);
    v_normal = normal;
    v_binormal = a_binormal;
    v_tangent = a_tangent;

    vec4 fpos = u_mvpMatrix * position;
    v_depth = fpos.z;

    gl_Position = fpos;
    v_texCoord = a_texCoord0;
}
