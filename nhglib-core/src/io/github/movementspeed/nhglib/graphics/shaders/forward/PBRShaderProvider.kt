package io.github.movementspeed.nhglib.graphics.shaders.forward

import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.Shader
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider
import io.github.movementspeed.nhglib.utils.data.Bundle
import io.github.movementspeed.nhglib.utils.data.Strings
import io.github.movementspeed.nhglib.utils.graphics.ShaderUtils

/**
 * Created by Fausto Napoli on 20/03/2017.
 */
class PBRShaderProvider : BaseShaderProvider() {
    override fun createShader(renderable: Renderable): Shader {
        val params = createShaderParams(renderable)
        return createShader(renderable, params)
    }

    protected fun createShaderParams(renderable: Renderable): PBRShader.Params {
        val params = PBRShader.Params()
        params.albedo = ShaderUtils.hasAlbedo(renderable)
        params.rma = ShaderUtils.hasRMA(renderable)
        params.normal = ShaderUtils.hasPbrNormal(renderable)
        params.useBones = ShaderUtils.useBones(renderable)
        params.lit = ShaderUtils.hasLights(renderable.environment)
        params.gammaCorrection = ShaderUtils.useGammaCorrection(renderable.environment)
        params.imageBasedLighting = ShaderUtils.useImageBasedLighting(renderable.environment)

        if (renderable.userData != null) {
            val bundle = renderable.userData as Bundle
            val forceUnlit = bundle.getBoolean(Strings.RenderingSettings.forceUnlitKey, false)

            if (forceUnlit) {
                params.lit = false
                params.imageBasedLighting = false
            }
        }

        return params
    }

    protected fun createShader(renderable: Renderable, params: PBRShader.Params): Shader {
        return PBRShader(renderable, renderable.environment, params)
    }
}