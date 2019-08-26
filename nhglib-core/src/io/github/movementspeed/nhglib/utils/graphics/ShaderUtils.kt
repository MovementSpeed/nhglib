package io.github.movementspeed.nhglib.utils.graphics

import com.badlogic.gdx.graphics.VertexAttribute
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.attributes.*
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.environment.PointLight
import com.badlogic.gdx.graphics.g3d.environment.SpotLight
import com.badlogic.gdx.utils.Array
import io.github.movementspeed.nhglib.graphics.lights.NhgLightsAttribute
import io.github.movementspeed.nhglib.graphics.shaders.attributes.GammaCorrectionAttribute
import io.github.movementspeed.nhglib.graphics.shaders.attributes.IBLAttribute
import io.github.movementspeed.nhglib.graphics.shaders.attributes.PBRColorAttribute
import io.github.movementspeed.nhglib.graphics.shaders.attributes.PBRTextureAttribute

/**
 * Created by Fausto Napoli on 13/03/2017.
 */
object ShaderUtils {
    fun createPrefix(renderable: Renderable, skinned: Boolean): String {
        var prefix = ""

        renderable.meshPart.mesh.vertexAttributes.forEach {
            if (it.usage == VertexAttributes.Usage.BoneWeight) {
                prefix += "#define boneWeight" + it.unit + "Flag\n"
            }
        }

        if (skinned) {
            prefix += "#define skinningFlag\n"
        }

        val environment = renderable.environment

        // Ambient lighting
        val ambientLightAttribute = environment.get(ColorAttribute.AmbientLight) as? ColorAttribute

        if (ambientLightAttribute != null) {
            prefix += "#define ambientLighting\n"
        }

        // Directional lighting
        val directionalLightsAttribute = environment.get(DirectionalLightsAttribute.Type) as? DirectionalLightsAttribute

        if (directionalLightsAttribute != null) {
            val directionalLights = directionalLightsAttribute.lights

            if (directionalLights.size > 0) {
                prefix += "#define numDirectionalLights " + directionalLights.size + "\n"
            }
        }

        // Point lighting
        val pointLightsAttribute = environment.get(PointLightsAttribute.Type) as? PointLightsAttribute

        if (pointLightsAttribute != null) {
            val pointLights = pointLightsAttribute.lights

            if (pointLights.size > 0) {
                prefix += "#define numPointLights " + pointLights.size + "\n"
            }
        }

        // Spot lighting
        val spotLightsAttribute = environment.get(SpotLightsAttribute.Type) as? SpotLightsAttribute

        if (spotLightsAttribute != null) {
            val spotLights = spotLightsAttribute.lights

            if (spotLights.size > 0) {
                prefix += "#define numSpotLights " + spotLights.size + "\n"
            }
        }

        return prefix
    }

    fun isRenderableSkinned(renderable: Renderable) = renderable.bones != null && renderable.bones.isNotEmpty()

    fun hasDiffuse(renderable: Renderable): Boolean {
        var res = false
        val attribute = renderable.material.get(TextureAttribute.Diffuse) as? TextureAttribute

        if (attribute != null && attribute.textureDescription.texture != null) {
            res = true
        }

        return res
    }

    fun hasNormal(renderable: Renderable): Boolean {
        var res = false
        val attribute = renderable.material.get(TextureAttribute.Normal) as? TextureAttribute

        if (attribute != null && attribute.textureDescription.texture != null) {
            res = true
        }

        return res
    }

    fun hasSpecular(renderable: Renderable): Boolean {
        var res = false
        val attribute = renderable.material.get(TextureAttribute.Specular) as? TextureAttribute

        if (attribute != null && attribute.textureDescription.texture != null) {
            res = true
        }

        return res
    }

    fun hasBump(renderable: Renderable): Boolean {
        var res = false
        val attribute = renderable.material.get(TextureAttribute.Bump) as? TextureAttribute

        if (attribute != null && attribute.textureDescription.texture != null) {
            res = true
        }

        return res
    }

    fun hasAlbedo(renderable: Renderable): Boolean {
        var res = false
        val attribute = renderable.material.get(PBRTextureAttribute.Albedo) as? PBRTextureAttribute

        if (attribute != null && attribute.textureDescription.texture != null) {
            res = true
        }

        return res
    }

    fun hasAlbedoColor(renderable: Renderable): Boolean {
        var res = false
        val attribute = renderable.material.get(PBRColorAttribute.AlbedoColor) as? PBRColorAttribute

        if (attribute != null) {
            res = true
        }

        return res
    }

    fun hasRMA(renderable: Renderable): Boolean {
        var res = false
        val attribute = renderable.material.get(PBRTextureAttribute.RMA) as? PBRTextureAttribute

        if (attribute != null && attribute.textureDescription.texture != null) {
            res = true
        }

        return res
    }

    fun hasEmissive(renderable: Renderable): Boolean {
        var res = false
        val attribute = renderable.material.get(PBRTextureAttribute.Emissive) as? PBRTextureAttribute

        if (attribute != null && attribute.textureDescription.texture != null) {
            res = true
        }

        return res
    }

    fun hasPbrNormal(renderable: Renderable): Boolean {
        var res = false
        val attribute = renderable.material.get(PBRTextureAttribute.Normal) as? PBRTextureAttribute

        if (attribute != null && attribute.textureDescription.texture != null) {
            res = true
        }

        return res
    }

    fun useBones(renderable: Renderable) = renderable.bones != null && renderable.bones.isNotEmpty()
    }

    fun hasLights(environment: Environment): Boolean {
        val lightsAttribute = environment.get(NhgLightsAttribute.Type) as? NhgLightsAttribute
        return lightsAttribute != null && lightsAttribute.lights.size > 0
    }

    fun useGammaCorrection(environment: Environment): Boolean {
        val gammaCorrectionAttribute = environment.get(GammaCorrectionAttribute.Type) as? GammaCorrectionAttribute
        return gammaCorrectionAttribute == null || gammaCorrectionAttribute.gammaCorrection
    }

    fun useImageBasedLighting(environment: Environment): Boolean {
        val res: Boolean

        val iblAttribute = environment.get(IBLAttribute.IrradianceType) as? IBLAttribute
        res = iblAttribute != null && iblAttribute.textureDescription != null

        return res
    }
}