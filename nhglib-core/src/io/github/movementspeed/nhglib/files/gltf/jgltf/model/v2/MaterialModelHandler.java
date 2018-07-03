/*
 * www.javagl.de - JglTF
 *
 * Copyright 2015-2016 Marco Hutter - http://www.javagl.de
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package io.github.movementspeed.nhglib.files.gltf.jgltf.model.v2;

import io.github.movementspeed.nhglib.files.gltf.jgltf.impl.v2.*;
import io.github.movementspeed.nhglib.files.gltf.jgltf.model.GltfConstants;
import io.github.movementspeed.nhglib.files.gltf.jgltf.model.MaterialModel;
import io.github.movementspeed.nhglib.files.gltf.jgltf.model.NodeModel;
import io.github.movementspeed.nhglib.files.gltf.jgltf.model.Optionals;
import io.github.movementspeed.nhglib.files.gltf.jgltf.model.gl.*;
import io.github.movementspeed.nhglib.files.gltf.jgltf.model.gl.ShaderModel.ShaderType;
import io.github.movementspeed.nhglib.files.gltf.jgltf.model.gl.impl.*;
import io.github.movementspeed.nhglib.files.gltf.jgltf.model.impl.DefaultMaterialModel;
import io.github.movementspeed.nhglib.files.gltf.jgltf.model.io.Buffers;
import io.github.movementspeed.nhglib.files.gltf.jgltf.model.io.IO;
import io.github.movementspeed.nhglib.files.gltf.jgltf.model.v2.gl.Materials;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class for creating the {@link MaterialModel} instances that are required
 * in a {@link GltfModelV2}. <br>
 * <br>
 * It will lazily create the internal {@link TechniqueModel},
 * {@link ProgramModel} and {@link ShaderModel} instances that
 * are required for rendering.
 */
class MaterialModelHandler {
    /**
     * The logger used in this class
     */
    private static final Logger logger =
            Logger.getLogger(MaterialModelHandler.class.getName());

    /**
     * The mapping from joint counts to vertex {@link ShaderModel} instances
     */
    private final Map<Integer, ShaderModel> vertexShaderModels;

    /**
     * The fragment {@link ShaderModel}
     */
    private ShaderModel fragmentShaderModel;

    /**
     * The mapping from joint count to {@link ProgramModel} instances
     */
    private final Map<Integer, ProgramModel> programModels;

    /**
     * The mapping from {@link MaterialStructure} descriptions to the
     * matching {@link TechniqueModel} instances
     */
    private final Map<MaterialStructure, TechniqueModel> techniqueModels;

    /**
     * Default constructor
     */
    MaterialModelHandler() {
        this.vertexShaderModels =
                new LinkedHashMap<Integer, ShaderModel>();
        this.programModels =
                new LinkedHashMap<Integer, ProgramModel>();
        this.techniqueModels =
                new LinkedHashMap<MaterialStructure, TechniqueModel>();
    }

    /**
     * Obtain the vertex {@link ShaderModel} for the given number of joints,
     * creating it if necessary
     *
     * @param numJoints The number of joints
     * @return The {@link ShaderModel}
     */
    private ShaderModel obtainVertexShaderModel(int numJoints) {
        ShaderModel shaderModel = vertexShaderModels.get(numJoints);
        if (shaderModel == null) {
            shaderModel = createVertexShaderModel(numJoints);
            vertexShaderModels.put(numJoints, shaderModel);
        }
        return shaderModel;
    }

    /**
     * Create the vertex {@link ShaderModel} for the given number of joints
     *
     * @param numJoints The number of joints
     * @return The {@link ShaderModel}
     */
    private ShaderModel createVertexShaderModel(int numJoints) {
        String vertexShaderDefines = "";
        if (numJoints > 0) {
            vertexShaderDefines += "#define NUM_JOINTS " + numJoints + "\n";
        }
        ShaderModel vertexShaderModel = createDefaultShaderModel(
                "pbr.vert", "pbr" + numJoints + ".vert",
                ShaderType.VERTEX_SHADER, vertexShaderDefines);
        return vertexShaderModel;
    }

    /**
     * Obtain the fragment {@link ShaderModel}, creating it if necessary
     *
     * @return The {@link ShaderModel}
     */
    private ShaderModel obtainFragmentShaderModel() {
        if (fragmentShaderModel == null) {
            fragmentShaderModel = createDefaultShaderModel(
                    "pbr.frag", "pbr.frag", ShaderType.FRAGMENT_SHADER, null);
        }
        return fragmentShaderModel;
    }


    /**
     * Obtain the {@link ProgramModel} for the given number of joints,
     * creating it if necessary
     *
     * @param numJoints The number of joints
     * @return The {@link ProgramModel}
     */
    private ProgramModel obtainProgramModel(int numJoints) {
        ProgramModel programModel = programModels.get(numJoints);
        if (programModel == null) {
            programModel = createProgramModel(numJoints);
            programModels.put(numJoints, programModel);
        }
        return programModel;
    }

    /**
     * Create the vertex {@link ProgramModel} for the given number of joints
     *
     * @param numJoints The number of joints
     * @return The {@link ProgramModel}
     */
    private ProgramModel createProgramModel(int numJoints) {
        ShaderModel vertexShaderModel =
                obtainVertexShaderModel(numJoints);
        ShaderModel fragmentShaderModel =
                obtainFragmentShaderModel();

        DefaultProgramModel programModel = new DefaultProgramModel();
        programModel.setVertexShaderModel(vertexShaderModel);
        programModel.setFragmentShaderModel(fragmentShaderModel);

        return programModel;
    }


    /**
     * Obtain the {@link TechniqueModel} for the given
     * {@link MaterialStructure}, creating it if necessary
     *
     * @param materialStructure The {@link MaterialStructure}
     * @return The {@link TechniqueModel}
     */
    private TechniqueModel obtainTechniqueModel(
            MaterialStructure materialStructure) {
        TechniqueModel techniqueModel = techniqueModels.get(materialStructure);
        if (techniqueModel == null) {
            techniqueModel = createTechniqueModel(materialStructure);
            techniqueModels.put(materialStructure, techniqueModel);
        }
        return techniqueModel;
    }


    /**
     * Create the {@link TechniqueModel} for the given
     * {@link MaterialStructure}
     *
     * @param materialStructure The {@link MaterialStructure}
     * @return The {@link TechniqueModel}
     */
    private TechniqueModel createTechniqueModel(
            MaterialStructure materialStructure) {
        ProgramModel programModel =
                obtainProgramModel(materialStructure.getNumJoints());

        DefaultTechniqueModel techniqueModel = new DefaultTechniqueModel();
        techniqueModel.setProgramModel(programModel);

        addParametersForPbrTechnique(techniqueModel, materialStructure);

        TechniqueStatesModel techniqueStatesModel =
                TechniqueStatesModels.createDefault();
        techniqueModel.setTechniqueStatesModel(techniqueStatesModel);

        return techniqueModel;
    }


    /**
     * Create a {@link MaterialModel} instance for the given {@link Material}
     *
     * @param material  The {@link Material}
     * @param numJoints The number of joints
     * @return The {@link MaterialModel}
     */
    DefaultMaterialModel createMaterialModel(Material material, int numJoints) {
        DefaultMaterialModel materialModel = new DefaultMaterialModel();

        MaterialStructure materialStructure =
                new MaterialStructure(material, numJoints);
        TechniqueModel techniqueModel =
                obtainTechniqueModel(materialStructure);
        materialModel.setTechniqueModel(techniqueModel);

        MaterialPbrMetallicRoughness pbrMetallicRoughness =
                material.getPbrMetallicRoughness();
        if (pbrMetallicRoughness == null) {
            pbrMetallicRoughness =
                    Materials.createDefaultMaterialPbrMetallicRoughness();
        }

        Map<String, Object> values = new LinkedHashMap<String, Object>();

        if (Boolean.TRUE.equals(material.isDoubleSided())) {
            values.put("isDoubleSided", 1);
        } else {
            values.put("isDoubleSided", 0);
        }

        TextureInfo baseColorTextureInfo =
                pbrMetallicRoughness.getBaseColorTexture();
        if (baseColorTextureInfo != null) {
            values.put("hasBaseColorTexture", 1);
            values.put("baseColorTexCoord",
                    materialStructure.getBaseColorTexCoordSemantic());
            values.put("baseColorTexture",
                    baseColorTextureInfo.getIndex());
        } else {
            values.put("hasBaseColorTexture", 0);
        }
        float[] baseColorFactor = Optionals.of(
                pbrMetallicRoughness.getBaseColorFactor(),
                pbrMetallicRoughness.defaultBaseColorFactor());
        values.put("baseColorFactor", baseColorFactor);


        TextureInfo metallicRoughnessTextureInfo =
                pbrMetallicRoughness.getMetallicRoughnessTexture();
        if (metallicRoughnessTextureInfo != null) {
            values.put("hasMetallicRoughnessTexture", 1);
            values.put("metallicRoughnessTexCoord",
                    materialStructure.getMetallicRoughnessTexCoordSemantic());
            values.put("metallicRoughnessTexture",
                    metallicRoughnessTextureInfo.getIndex());
        } else {
            values.put("hasMetallicRoughnessTexture", 0);
        }
        float metallicFactor = Optionals.of(
                pbrMetallicRoughness.getMetallicFactor(),
                pbrMetallicRoughness.defaultMetallicFactor());
        values.put("metallicFactor", metallicFactor);

        float roughnessFactor = Optionals.of(
                pbrMetallicRoughness.getRoughnessFactor(),
                pbrMetallicRoughness.defaultRoughnessFactor());
        values.put("roughnessFactor", roughnessFactor);


        MaterialNormalTextureInfo normalTextureInfo =
                material.getNormalTexture();
        if (normalTextureInfo != null) {
            values.put("hasNormalTexture", 1);
            values.put("normalTexCoord",
                    materialStructure.getNormalTexCoordSemantic());
            values.put("normalTexture",
                    normalTextureInfo.getIndex());

            float normalScale = Optionals.of(
                    normalTextureInfo.getScale(),
                    normalTextureInfo.defaultScale());
            values.put("normalScale", normalScale);
        } else {
            values.put("hasNormalTexture", 0);
            values.put("normalScale", 1.0);
        }

        MaterialOcclusionTextureInfo occlusionTextureInfo =
                material.getOcclusionTexture();
        if (occlusionTextureInfo != null) {
            values.put("hasOcclusionTexture", 1);
            values.put("occlusionTexCoord",
                    materialStructure.getOcclusionTexCoordSemantic());
            values.put("occlusionTexture",
                    occlusionTextureInfo.getIndex());

            float occlusionStrength = Optionals.of(
                    occlusionTextureInfo.getStrength(),
                    occlusionTextureInfo.defaultStrength());
            values.put("occlusionStrength", occlusionStrength);
        } else {
            values.put("hasOcclusionTexture", 0);

            // TODO Should this really be 1.0?
            values.put("occlusionStrength", 0.0);
        }

        TextureInfo emissiveTextureInfo =
                material.getEmissiveTexture();
        if (emissiveTextureInfo != null) {
            values.put("hasEmissiveTexture", 1);
            values.put("emissiveTexCoord",
                    materialStructure.getEmissiveTexCoordSemantic());
            values.put("emissiveTexture",
                    emissiveTextureInfo.getIndex());
        } else {
            values.put("hasEmissiveTexture", 0);
        }

        float[] emissiveFactor = Optionals.of(
                material.getEmissiveFactor(),
                material.defaultEmissiveFactor());
        values.put("emissiveFactor", emissiveFactor);


        float lightPosition[] = {-800, 500, 500};
        values.put("lightPosition", lightPosition);


        materialModel.setValues(values);

        return materialModel;
    }

    private static String fragmentShader = "// References:\n" +
            "// [1] : Real Shading in Unreal Engine 4 (B. Karis)\n" +
            "//       http://blog.selfshadow.com/publications/s2013-shading-course/karis/s2013_pbs_epic_notes_v2.pdf\n" +
            "// [2] : Moving Frostbite to Physically Based Rendering 2.0 (S. Lagarde)\n" +
            "//       http://www.frostbite.com/wp-content/uploads/2014/11/course_notes_moving_frostbite_to_pbr_v2.pdf\n" +
            "// [3] : Microfacet Models for Refraction through Rough Surfaces (B. Walter)\n" +
            "//       http://www.cs.cornell.edu/~srm/publications/EGSR07-btdf.pdf\n" +
            "// [4] : An Inexpensive BRDF Model for Physically-based Rendering (C. Schlick)\n" +
            "//       https://www.cs.virginia.edu/~jdl/bib/appearance/analytic models/schlick94b.pdf\n" +
            "// [5] : A Reflectance Model for Computer Graphics (R. Cook)\n" +
            "//       http://graphics.pixar.com/library/ReflectanceModel/paper.pdf\n" +
            "// [6] : Crafting a Next-Gen Material Pipeline for The Order: 1886 (D. Neubelt)\n" +
            "//       http://blog.selfshadow.com/publications/s2013-shading-course/rad/s2013_pbs_rad_notes.pdf\n" +
            "\n" +
            "precision mediump float;\n" +
            "\n" +
            "uniform sampler2D u_baseColorTexture;\n" +
            "uniform sampler2D u_metallicRoughnessTexture;\n" +
            "uniform sampler2D u_normalTexture;\n" +
            "uniform sampler2D u_occlusionTexture;\n" +
            "uniform sampler2D u_emissiveTexture;\n" +
            "\n" +
            "uniform int u_hasBaseColorTexture;\n" +
            "uniform int u_hasMetallicRoughnessTexture;\n" +
            "uniform int u_hasNormalTexture;\n" +
            "uniform int u_hasOcclusionTexture;\n" +
            "uniform int u_hasEmissiveTexture;\n" +
            "\n" +
            "uniform vec4 u_baseColorFactor;\n" +
            "uniform float u_metallicFactor;\n" +
            "uniform float u_roughnessFactor;\n" +
            "uniform float u_normalScale;\n" +
            "uniform float u_occlusionStrength;\n" +
            "uniform vec3 u_emissiveFactor;\n" +
            "\n" +
            "uniform int u_isDoubleSided;\n" +
            "\n" +
            "varying vec3 v_lightPosition;\n" +
            "\n" +
            "varying vec2 v_baseColorTexCoord;\n" +
            "varying vec2 v_metallicRoughnessTexCoord;\n" +
            "varying vec2 v_normalTexCoord;\n" +
            "varying vec2 v_occlusionTexCoord;\n" +
            "varying vec2 v_emissiveTexCoord;\n" +
            "\n" +
            "varying vec3 v_position;\n" +
            "varying vec3 v_normal;\n" +
            "varying vec4 v_tangent;\n" +
            "\n" +
            "const float M_PI = 3.141592653589793;\n" +
            "\n" +
            "// Computation of the specular distribution of microfacet normals on the\n" +
            "// surface. This is also referred to as \"NDF\", the \"normal distribution\n" +
            "// function\". It receives the half-vector H, the surface normal N, and the\n" +
            "// roughness. This implementation is based on the description in [1], which\n" +
            "// is supposed to be a summary of [3], although I didn't do the maths...\n" +
            "\n" +
            "float computeMicrofacetDistribution(\n" +
            "    vec3 H, vec3 N, float roughness)\n" +
            "{\n" +
            "    float alpha = roughness * roughness;\n" +
            "    float alpha_squared = alpha * alpha;\n" +
            "\n" +
            "    float NdotH = clamp(dot(N, H), 0.0, 1.0);\n" +
            "    float NdotH_squared = NdotH * NdotH;\n" +
            "\n" +
            "    float x = NdotH_squared * (alpha_squared - 1.0) + 1.0;\n" +
            "    return alpha_squared / (M_PI * x * x);\n" +
            "}\n" +
            "\n" +
            "// Computation of the Fresnel specular reflectance, using the approximation\n" +
            "// described in [4]. It receives the specular color, the\n" +
            "// direction from the surface point to the viewer V, and the half-vector H.\n" +
            "\n" +
            "vec3 computeSpecularReflectance(\n" +
            "    vec3 specularColor, vec3 V, vec3 H)\n" +
            "{\n" +
            "    float HdotV = clamp(dot(H, V), 0.0, 1.0);\n" +
            "    return specularColor + (1.0 - specularColor) * pow(1.0 - HdotV, 5.0);\n" +
            "}\n" +
            "\n" +
            "// Computation of the geometric shadowing or \"specular geometric attenuation\".\n" +
            "// This describes how much the microfacets of the surface shadow each other,\n" +
            "// based on the roughness of the surface.\n" +
            "// This implementation is based on [1], which contains some odd tweaks and\n" +
            "// cross-references to [3] and [4], which I did not follow in all depth.\n" +
            "// Let's hope they know their stuff.\n" +
            "// It receives the roughness value, the normal vector of the surface N,\n" +
            "// the vector from the surface to the viewer V, the vector from the\n" +
            "// surface to the light L, and the half vector H\n" +
            "\n" +
            "float computeSpecularAttenuation(\n" +
            "    float roughness, vec3 V, vec3 N, vec3 L, vec3 H)\n" +
            "{\n" +
            "    float NdotL = clamp(dot(N, L), 0.0, 1.0);\n" +
            "    float NdotV = clamp(dot(N, V), 0.0, 1.0);\n" +
            "    float k = (roughness + 1.0) * (roughness + 1.0) / 8.0;\n" +
            "\n" +
            "    float GL = NdotL / (NdotL * (1.0 - k) + k);\n" +
            "    float GV = NdotV / (NdotV * (1.0 - k) + k);\n" +
            "\n" +
            "    return GL * GV;\n" +
            "}\n" +
            "\n" +
            "// Compute the BRDF, as it is described in [1], with a reference\n" +
            "// to [5], although the formula does not seem to appear there.\n" +
            "// The inputs are the base color and metallic/roughness values,\n" +
            "// the normal vector of the surface N, the vector from the surface \n" +
            "// to the viewer V, the vector from the surface to the light L, \n" +
            "// and the half vector H\n" +
            "vec3 computeSpecularBRDF(\n" +
            "    vec4 baseColor, float metallic, float roughness, \n" +
            "    vec3 V, vec3 N, vec3 L, vec3 H)\n" +
            "{\n" +
            "    // Compute the microfacet distribution (D)\n" +
            "    float microfacetDistribution =\n" +
            "        computeMicrofacetDistribution(H, N, roughness);\n" +
            "\n" +
            "    // Compute the specularly reflected color (F)\n" +
            "    vec3 specularInputColor = (baseColor.rgb * metallic);\n" +
            "    vec3 specularReflectance =\n" +
            "        computeSpecularReflectance(specularInputColor, V, H);\n" +
            "\n" +
            "    // Compute the geometric specular attenuation (G)\n" +
            "    float specularAttenuation =\n" +
            "       computeSpecularAttenuation(roughness, V, N, L, H);\n" +
            "\n" +
            "    // The seemingly arbitrary clamping to avoid divide-by-zero\n" +
            "    // was inspired by [6].\n" +
            "    float NdotV = dot(N,V);\n" +
            "    float NdotL = dot(N,L);\n" +
            "    vec3 specularBRDF = vec3(0.0);\n" +
            "    if (NdotV > 0.0001 && NdotL > 0.0001)\n" +
            "    {\n" +
            "        float d = microfacetDistribution;\n" +
            "        vec3 f = specularReflectance;\n" +
            "        float g = specularAttenuation;\n" +
            "        specularBRDF = (d * f * g) / (4.0 * NdotL * NdotV);\n" +
            "    }\n" +
            "    \n" +
            "    return specularBRDF;\n" +
            "}\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "// Compute the base color from the baseColorFactor \n" +
            "// and (if present) the baseColorTexture \n" +
            "vec4 computeBaseColor()\n" +
            "{\n" +
            "    vec4 sampledBaseColor = vec4(1.0);\n" +
            "    if (u_hasBaseColorTexture != 0)\n" +
            "    {\n" +
            "        sampledBaseColor = texture2D(u_baseColorTexture, v_baseColorTexCoord);\n" +
            "    }\n" +
            "    vec4 baseColor = sampledBaseColor * u_baseColorFactor;\n" +
            "    return baseColor;\n" +
            "}\n" +
            "\n" +
            "// Compute the metallic and roughness values, from the metallicFactor and\n" +
            "// the roughnessFactor, and (if present) from the metallicRoughnessTexture \n" +
            "vec2 computeMetallicRoughness() \n" +
            "{\n" +
            "    vec4 sampledMetallicRoughness = vec4(1.0);\n" +
            "    if (u_hasMetallicRoughnessTexture != 0)\n" +
            "    {\n" +
            "        sampledMetallicRoughness = texture2D(u_metallicRoughnessTexture, v_metallicRoughnessTexCoord);\n" +
            "    }\n" +
            "    float metallic = sampledMetallicRoughness.b * u_metallicFactor;\n" +
            "    float roughness = sampledMetallicRoughness.g * u_roughnessFactor;\n" +
            "\treturn vec2(metallic, roughness);\n" +
            "}\n" +
            "\n" +
            "// Compute the normal N, from the normal attribute, and (if present)\n" +
            "// from the normalTexture\n" +
            "vec3 computeNormal()\n" +
            "{\n" +
            "    vec3 N = normalize(v_normal);\n" +
            "    \n" +
            "    // Fetch the normal from the normal texture\n" +
            "    if (u_hasNormalTexture != 0)\n" +
            "    {\n" +
            "        vec3 sampledNormal = texture2D(u_normalTexture, v_normalTexCoord).rgb;\n" +
            "        vec3 textureNormal = normalize(sampledNormal * 2.0 - 1.0);\n" +
            "        vec3 scaledTextureNormal = textureNormal * u_normalScale;\n" +
            "\n" +
            "        // Compute the TBN (tangent, bitangent, normal) matrix\n" +
            "        // that maps the normal of the normal texture from the\n" +
            "        // surface coordinate system into view space. \n" +
            "        // The w-component of the tangent attribute value indicates\n" +
            "        // the handedness of the tangent space (+1 or -1)\n" +
            "        vec3 T = normalize(v_tangent.xyz);\n" +
            "        vec3 B = cross(N, T) * v_tangent.w;\n" +
            "        mat3 TBN = mat3(T, B, N);\n" +
            "\n" +
            "        N = normalize(TBN * scaledTextureNormal);\n" +
            "    }\n" +
            "    \n" +
            "    // TODO gl_FrontFacing seems to always be \"true\"\n" +
            "    if (!gl_FrontFacing) \n" +
            "    {\n" +
            "        if (u_isDoubleSided != 0) \n" +
            "        {\n" +
            "            N = -N;\n" +
            "        }\n" +
            "    } \n" +
            "    \n" +
            "    return N;\n" +
            "}\n" +
            "\n" +
            "// Compute the occlusion factor, from the occlusionStrength\n" +
            "// and (if present) from the occlusionTexture\n" +
            "float computeOcclusionFactor()\n" +
            "{\n" +
            "    float sampledOcclusion = 1.0;\n" +
            "    if (u_hasOcclusionTexture != 0)\n" +
            "    {\n" +
            "        sampledOcclusion = texture2D(u_occlusionTexture, v_occlusionTexCoord).r;\n" +
            "    }\n" +
            "    float occlusionFactor = 1.0 - ((1.0 - sampledOcclusion) * u_occlusionStrength);\n" +
            "    return occlusionFactor;\n" +
            "}\n" +
            "\n" +
            "// Compute the emissive components, from the emissiveFactor and (if present)\n" +
            "// from the emissiveTexture\n" +
            "vec4 computeEmissive()\n" +
            "{\n" +
            "    vec4 sampledEmissive = vec4(1.0);\n" +
            "    if (u_hasEmissiveTexture != 0)\n" +
            "    {\n" +
            "        sampledEmissive = texture2D(u_emissiveTexture, v_emissiveTexCoord);\n" +
            "    }\n" +
            "    vec4 emissive = sampledEmissive * vec4(u_emissiveFactor, 1.0);\n" +
            "    return emissive;\n" +
            "}\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "\tvec4 baseColor = computeBaseColor();\n" +
            "\t\n" +
            "    vec2 metallicRoughness = computeMetallicRoughness();\n" +
            "    float metallic = metallicRoughness.x;\n" +
            "    float roughness = metallicRoughness.y;\n" +
            "\n" +
            "\tvec3 N = computeNormal();\n" +
            "    \n" +
            "    // Compute the vector from the surface point to the light (L),\n" +
            "    // the vector from the surface point to the viewer (V),\n" +
            "    // and the half-vector between both (H)\n" +
            "    // The camera position in view space is fixed.\n" +
            "    vec3 cameraPosition = vec3(0.0, 0.0, 1.0);\n" +
            "    vec3 L = normalize(v_lightPosition - v_position);\n" +
            "    vec3 V = normalize(cameraPosition - v_position);\n" +
            "    vec3 H = normalize(L + V);\n" +
            "\n" +
            "    vec3 specularBRDF = computeSpecularBRDF(\n" +
            "    \tbaseColor, metallic, roughness, V, N, L, H);\n" +
            "\n" +
            "    float NdotL = dot(N,L);\n" +
            "    vec3 diffuseColor = baseColor.rgb * (1.0 - metallic);\n" +
            "    vec4 diffuse = vec4(diffuseColor * NdotL, 1.0);\n" +
            "\n" +
            "    vec3 specularInputColor = (baseColor.rgb * metallic);\n" +
            "    vec4 specular = vec4(specularInputColor * specularBRDF, 1.0);\n" +
            "\n" +
            "    float occlusionFactor = computeOcclusionFactor();\n" +
            "\n" +
            "    vec4 emissive = computeEmissive();\n" +
            "\n" +
            "    vec4 mainColor = clamp(diffuse + specular, 0.0, 1.0);\n" +
            "    vec4 finalColor = clamp(mainColor * occlusionFactor + emissive, 0.0, 1.0);\n" +
            "\n" +
            "    //finalColor = vec4(0.0, 0.0, 0.0, 1.0);\n" +
            "    //finalColor.r = u_metallicFactor;\n" +
            "    //finalColor.g = u_roughnessFactor;\n" +
            "    //finalColor = vec4(1,1,0,1);\n" +
            "    //finalColor = diffuse;\n" +
            "    \n" +
            "    gl_FragColor = finalColor;\n" +
            "}\n" +
            "\n";

    private static String vertexShader = "attribute vec4 a_position;\n" +
            "attribute vec4 a_normal;\n" +
            "attribute vec4 a_tangent;\n" +
            "\n" +
            "attribute vec4 a_joint;\n" +
            "attribute vec4 a_weight;\n" +
            "\n" +
            "attribute vec2 a_baseColorTexCoord;\n" +
            "attribute vec2 a_metallicRoughnessTexCoord;\n" +
            "attribute vec2 a_normalTexCoord;\n" +
            "attribute vec2 a_occlusionTexCoord;\n" +
            "attribute vec2 a_emissiveTexCoord;\n" +
            "\n" +
            "uniform mat4 u_modelViewMatrix;\n" +
            "uniform mat4 u_projectionMatrix;\n" +
            "uniform mat3 u_normalMatrix;\n" +
            "\n" +
            "#ifdef NUM_JOINTS \n" +
            "uniform mat4 u_jointMat[NUM_JOINTS];\n" +
            "#endif \n" +
            "\n" +
            "uniform vec3 u_lightPosition = vec3(-800,500,500);\n" +
            "\n" +
            "varying vec3 v_position;\n" +
            "varying vec3 v_normal;\n" +
            "varying vec4 v_tangent;\n" +
            "\n" +
            "varying vec3 v_lightPosition;\n" +
            "\n" +
            "varying vec2 v_baseColorTexCoord;\n" +
            "varying vec2 v_metallicRoughnessTexCoord;\n" +
            "varying vec2 v_normalTexCoord;\n" +
            "varying vec2 v_occlusionTexCoord;\n" +
            "varying vec2 v_emissiveTexCoord;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    mat4 skinningMatrix = mat4(1.0);\n" +
            "    \n" +
            "#ifdef NUM_JOINTS \n" +
            "    skinningMatrix  = a_weight.x * u_jointMat[int(a_joint.x)];\n" +
            "    skinningMatrix += a_weight.y * u_jointMat[int(a_joint.y)];\n" +
            "    skinningMatrix += a_weight.z * u_jointMat[int(a_joint.z)];\n" +
            "    skinningMatrix += a_weight.w * u_jointMat[int(a_joint.w)];\n" +
            "#endif\n" +
            "    \n" +
            "    vec4 pos = u_modelViewMatrix * skinningMatrix * a_position;\n" +
            "    v_position = vec3(pos.xyz) / pos.w;\n" +
            "    v_normal = u_normalMatrix * vec3(a_normal);\n" +
            "    v_tangent = vec4(u_normalMatrix * vec3(a_tangent), a_tangent.w);\n" +
            "\n" +
            "    v_lightPosition = vec3(u_modelViewMatrix * vec4(u_lightPosition, 1.0));\n" +
            "\n" +
            "    v_baseColorTexCoord = a_baseColorTexCoord;\n" +
            "    v_metallicRoughnessTexCoord = a_metallicRoughnessTexCoord;\n" +
            "    v_normalTexCoord = a_normalTexCoord;\n" +
            "    v_occlusionTexCoord = a_occlusionTexCoord;\n" +
            "    v_emissiveTexCoord = a_emissiveTexCoord;\n" +
            "\n" +
            "    gl_Position = u_projectionMatrix * vec4(v_position, 1.0);\n" +
            "}\n" +
            "\n" +
            "\n";


    /**
     * Create a default {@link ShaderModel} instance with the given URI
     * string and type, by reading the resource that is identified with
     * the given name. If the specified resource cannot be read, then
     * an error message will be printed and the returned shader model
     * will not contain any data. This method is only intended for
     * internal use!
     *
     * @param resourceName The name of the resource to read the source code from
     * @param uriString    The URI string
     * @param shaderType   The shader type
     * @param defines      An optional string containing lines of code that
     *                     will be prefixed to the shader code, and which will usually
     *                     contain preprocessor definitions
     * @return The {@link ShaderModel}
     */
    private static DefaultShaderModel createDefaultShaderModel(String resourceName, String uriString,
                                                               ShaderType shaderType, String defines) {
        DefaultShaderModel shaderModel = new DefaultShaderModel(uriString, shaderType);
        String basicShaderString = "";

        switch (shaderType) {
            case VERTEX_SHADER:
                basicShaderString = vertexShader;
                break;

            case FRAGMENT_SHADER:
                basicShaderString = fragmentShader;
                break;
        }

        String fullShaderString = basicShaderString;
        if (defines != null) {
            fullShaderString = defines + "\n" + basicShaderString;
        }
        ByteBuffer shaderData =
                Buffers.create(fullShaderString.getBytes());
        shaderModel.setShaderData(shaderData);
        return shaderModel;
    }


    /**
     * Add all {@link TechniqueParametersModel} instances for PBR techniques
     * to the given {@link TechniqueModel}
     *
     * @param techniqueModel    The {@link TechniqueModel}
     * @param materialStructure The {@link MaterialStructure} of the material
     *                          for which the {@link TechniqueModel} is intended
     */
    private static void addParametersForPbrTechnique(
            DefaultTechniqueModel techniqueModel,
            MaterialStructure materialStructure) {
        addAttributeParameters(techniqueModel, "a_position",
                "position", GltfConstants.GL_FLOAT_VEC4, 1, "POSITION");
        addAttributeParameters(techniqueModel, "a_normal",
                "normal", GltfConstants.GL_FLOAT_VEC4, 1, "NORMAL");
        addAttributeParameters(techniqueModel, "a_tangent",
                "tangent", GltfConstants.GL_FLOAT_VEC4, 1, "TANGENT");

        addAttributeParameters(techniqueModel, "a_baseColorTexCoord",
                "baseColorTexCoord", GltfConstants.GL_FLOAT_VEC2, 1,
                materialStructure.getBaseColorTexCoordSemantic());
        addAttributeParameters(techniqueModel, "a_metallicRoughnessTexCoord",
                "metallicRoughnessTexCoord", GltfConstants.GL_FLOAT_VEC2, 1,
                materialStructure.getMetallicRoughnessTexCoordSemantic());
        addAttributeParameters(techniqueModel, "a_normalTexCoord",
                "normalTexCoord", GltfConstants.GL_FLOAT_VEC2, 1,
                materialStructure.getNormalTexCoordSemantic());
        addAttributeParameters(techniqueModel, "a_occlusionTexCoord",
                "occlusionTexCoord", GltfConstants.GL_FLOAT_VEC2, 1,
                materialStructure.getOcclusionTexCoordSemantic());
        addAttributeParameters(techniqueModel, "a_emissiveTexCoord",
                "emissiveTexCoord", GltfConstants.GL_FLOAT_VEC2, 1,
                materialStructure.getEmissiveTexCoordSemantic());

        addUniformParameters(techniqueModel, "u_modelViewMatrix",
                "modelViewMatrix", GltfConstants.GL_FLOAT_MAT4, 1, "MODELVIEW");
        addUniformParameters(techniqueModel, "u_projectionMatrix",
                "projectionMatrix", GltfConstants.GL_FLOAT_MAT4, 1, "PROJECTION");
        addUniformParameters(techniqueModel, "u_normalMatrix",
                "normalMatrix", GltfConstants.GL_FLOAT_MAT3, 1,
                "MODELVIEWINVERSETRANSPOSE");

        addUniformParameters(techniqueModel, "u_isDoubleSided",
                "isDoubleSided", GltfConstants.GL_INT, 1, null);

        addUniformParameters(techniqueModel, "u_baseColorTexture",
                "baseColorTexture", GltfConstants.GL_SAMPLER_2D, 1, null);
        addUniformParameters(techniqueModel, "u_metallicRoughnessTexture",
                "metallicRoughnessTexture", GltfConstants.GL_SAMPLER_2D, 1, null);
        addUniformParameters(techniqueModel, "u_normalTexture",
                "normalTexture", GltfConstants.GL_SAMPLER_2D, 1, null);
        addUniformParameters(techniqueModel, "u_occlusionTexture",
                "occlusionTexture", GltfConstants.GL_SAMPLER_2D, 1, null);
        addUniformParameters(techniqueModel, "u_emissiveTexture",
                "emissiveTexture", GltfConstants.GL_SAMPLER_2D, 1, null);

        addUniformParameters(techniqueModel, "u_hasBaseColorTexture",
                "hasBaseColorTexture", GltfConstants.GL_INT, 1, null);
        addUniformParameters(techniqueModel, "u_hasMetallicRoughnessTexture",
                "hasMetallicRoughnessTexture", GltfConstants.GL_INT, 1, null);
        addUniformParameters(techniqueModel, "u_hasNormalTexture",
                "hasNormalTexture", GltfConstants.GL_INT, 1, null);
        addUniformParameters(techniqueModel, "u_hasOcclusionTexture",
                "hasOcclusionTexture", GltfConstants.GL_INT, 1, null);
        addUniformParameters(techniqueModel, "u_hasEmissiveTexture",
                "hasEmissiveTexture", GltfConstants.GL_INT, 1, null);

        addUniformParameters(techniqueModel, "u_baseColorFactor",
                "baseColorFactor", GltfConstants.GL_FLOAT_VEC4, 1, null);
        addUniformParameters(techniqueModel, "u_metallicFactor",
                "metallicFactor", GltfConstants.GL_FLOAT, 1, null);
        addUniformParameters(techniqueModel, "u_roughnessFactor",
                "roughnessFactor", GltfConstants.GL_FLOAT, 1, null);
        addUniformParameters(techniqueModel, "u_normalScale",
                "normalScale", GltfConstants.GL_FLOAT, 1, null);
        addUniformParameters(techniqueModel, "u_occlusionStrength",
                "occlusionStrength", GltfConstants.GL_FLOAT, 1, null);
        addUniformParameters(techniqueModel, "u_emissiveFactor",
                "emissiveFactor", GltfConstants.GL_FLOAT_VEC3, 1, null);

        addAttributeParameters(techniqueModel, "a_joint",
                "joint", GltfConstants.GL_FLOAT_VEC4, 1, "JOINTS_0");
        addAttributeParameters(techniqueModel, "a_weight",
                "weight", GltfConstants.GL_FLOAT_VEC4, 1, "WEIGHTS_0");

        if (materialStructure.getNumJoints() > 0) {
            addUniformParameters(techniqueModel, "u_jointMat",
                    "jointMat", GltfConstants.GL_FLOAT_MAT4,
                    materialStructure.getNumJoints(), "JOINTMATRIX");
        }

        // TODO Preliminary uniform for a single point light
        addUniformParameters(techniqueModel, "u_lightPosition",
                "lightPosition", GltfConstants.GL_FLOAT_VEC3, 1, null);

    }

    /**
     * Add the specified attribute to the given model
     *
     * @param techniqueModel The {@link TechniqueModel}
     * @param attributeName  The attribute name
     * @param parameterName  The parameter name
     * @param type           The parameter type
     * @param count          The count
     * @param semantic       The semantic
     */
    private static void addAttributeParameters(
            DefaultTechniqueModel techniqueModel, String attributeName,
            String parameterName, int type, int count, String semantic) {
        techniqueModel.addAttribute(attributeName, parameterName);
        addParameters(techniqueModel, parameterName, type, count, semantic);
    }

    /**
     * Add the specified uniform to the given model
     *
     * @param techniqueModel The {@link TechniqueModel}
     * @param uniformName    The uniform name
     * @param parameterName  The parameter name
     * @param type           The parameter type
     * @param count          The count
     * @param semantic       The semantic
     */
    private static void addUniformParameters(
            DefaultTechniqueModel techniqueModel, String uniformName,
            String parameterName, int type, int count, String semantic) {
        techniqueModel.addUniform(uniformName, parameterName);
        addParameters(techniqueModel, parameterName, type, count, semantic);
    }

    /**
     * Add a {@link TechniqueParametersModel} with the given parameters to
     * the given {@link TechniqueModel}
     *
     * @param techniqueModel The {@link TechniqueModel}
     * @param parameterName  The parameter name
     * @param type           The parameter type
     * @param count          The count
     * @param semantic       The semantic
     */
    private static void addParameters(DefaultTechniqueModel techniqueModel,
                                      String parameterName, int type, int count, String semantic) {
        Object value = null;
        NodeModel nodeModel = null;
        TechniqueParametersModel techniqueParametersModel =
                new DefaultTechniqueParametersModel(
                        type, count, semantic, value, nodeModel);
        techniqueModel.addParameter(
                parameterName, techniqueParametersModel);
    }


}
