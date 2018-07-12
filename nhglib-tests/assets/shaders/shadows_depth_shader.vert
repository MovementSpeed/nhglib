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

uniform HIGHP mat4 u_mvpMatrix;
uniform HIGHP mat4 u_modelMatrix;

void main() {
    LOWP vec4 position;

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

    gl_Position = u_mvpMatrix * position;
}