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

attribute HIGHP vec4 a_position;
attribute HIGHP vec3 a_binormal;
attribute HIGHP vec3 a_tangent;
attribute LOWP vec2 a_texCoord0;
attribute LOWP vec3 a_normal;

#ifdef boneWeight0Flag
    #define boneWeightsFlag
    attribute LOWP vec2 a_boneWeight0;
#endif

#ifdef boneWeight1Flag
    attribute LOWP vec2 a_boneWeight1;
#endif

#ifdef boneWeight2Flag
    attribute LOWP vec2 a_boneWeight2;
#endif

#ifdef boneWeight3Flag
    attribute LOWP vec2 a_boneWeight3;
#endif

#ifdef boneWeight4Flag
    attribute LOWP vec2 a_boneWeight4;
#endif

#ifdef boneWeight5Flag
    attribute LOWP vec2 a_boneWeight5;
#endif

#ifdef boneWeight6Flag
    attribute LOWP vec2 a_boneWeight6;
#endif

#ifdef boneWeight7Flag
    attribute LOWP vec2 a_boneWeight7;
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
uniform HIGHP mat4 u_viewMatrix;
uniform HIGHP mat4 u_modelMatrix;

varying HIGHP vec3 v_position;
varying HIGHP vec3 v_binormal;
varying HIGHP vec3 v_tangent;
varying LOWP vec2 v_texCoord;
varying LOWP vec3 v_normal;

void main() {
    LOWP vec4 position;
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

    position = u_modelMatrix * skinning * a_position;
    normal = normalize(vec3(u_viewMatrix * u_modelMatrix * skinning * vec4(a_normal, 0.0)));

	v_position = vec3(u_viewMatrix * position);
    v_normal = normal;
    v_binormal = a_binormal;
    v_tangent = a_tangent;

    gl_Position = u_mvpMatrix * position;
    v_texCoord = a_texCoord0;
}
