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
    #define IN attribute
    #define OUT varying
#else
    #define IN in
    #define OUT out
#endif

IN HIGHP vec4 a_position;
IN HIGHP vec3 a_binormal;
IN HIGHP vec3 a_tangent;
IN HIGHP vec2 a_texCoord0;
IN LOWP vec3 a_normal;

#ifdef boneWeight0Flag
    #define boneWeightsFlag
    IN LOWP vec2 a_boneWeight0;
#endif

#ifdef boneWeight1Flag
    IN LOWP vec2 a_boneWeight1;
#endif

#ifdef boneWeight2Flag
    IN LOWP vec2 a_boneWeight2;
#endif

#ifdef boneWeight3Flag
    IN LOWP vec2 a_boneWeight3;
#endif

#ifdef boneWeight4Flag
    IN LOWP vec2 a_boneWeight4;
#endif

#ifdef boneWeight5Flag
    IN LOWP vec2 a_boneWeight5;
#endif

#ifdef boneWeight6Flag
    IN LOWP vec2 a_boneWeight6;
#endif

#ifdef boneWeight7Flag
    IN LOWP vec2 a_boneWeight7;
#endif

#if defined(numBones) && defined(boneWeightsFlag)
    #if (numBones > 0)
        #define skinningFlag
    #endif
#endif

#ifdef numBones
    #if numBones > 0
        uniform HIGHP mat4 u_bones[numBones];
    #endif
#endif

uniform HIGHP mat4 u_projViewTrans;
uniform HIGHP mat4 u_viewTrans;
uniform HIGHP mat4 u_worldTrans;
uniform HIGHP mat4 u_shadowMapProjViewTrans;

OUT HIGHP vec3 v_position;
OUT HIGHP vec3 v_binormal;
OUT HIGHP vec3 v_tangent;
OUT HIGHP vec4 v_shadowMapUv;
OUT HIGHP vec2 v_texCoord;
OUT LOWP vec3 v_normal;

void main() {
    HIGHP vec4 position;
    LOWP vec3 normal;

    #ifdef numBones
        HIGHP mat4 skinning = mat4(0.0);

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
        HIGHP mat4 skinning = mat4(1.0);
    #endif

    position = u_worldTrans * skinning * a_position;
    normal = normalize(vec3(u_viewTrans * u_worldTrans * skinning * vec4(a_normal, 0.0)));

	v_position = vec3(u_viewTrans * position);
    v_normal = normal;
    v_binormal = a_binormal;
    v_tangent = a_tangent;

    v_shadowMapUv = u_shadowMapProjViewTrans * position;

    gl_Position = u_projViewTrans * position;
    v_texCoord = a_texCoord0;
}
