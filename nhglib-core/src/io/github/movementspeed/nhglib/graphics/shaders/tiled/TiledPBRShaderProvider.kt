package io.github.movementspeed.nhglib.graphics.shaders.tiled

import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.Shader
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider
import com.badlogic.gdx.utils.GdxRuntimeException
import io.github.movementspeed.nhglib.utils.graphics.ShaderUtils
import io.github.movementspeed.nhglib.utils.graphics.hasLights
import io.github.movementspeed.nhglib.utils.graphics.useGammaCorrection
import io.github.movementspeed.nhglib.utils.graphics.useImageBasedLighting

/**
 * Created by Fausto Napoli on 20/03/2017.
 */
class TiledPBRShaderProvider(private val environment: Environment) : BaseShaderProvider() {

    override fun createShader(renderable: Renderable): Shader {
        val params = TiledPBRShader.Params()
        params.albedo = ShaderUtils.hasAlbedo(renderable)
        params.rma = ShaderUtils.hasRMA(renderable)
        params.normal = ShaderUtils.hasPbrNormal(renderable)
        params.emissive = ShaderUtils.hasEmissive(renderable)
        params.useBones = ShaderUtils.useBones(renderable)
        params.lit = hasLights(environment)
        params.gammaCorrection = useGammaCorrection(environment)
        params.imageBasedLighting = useImageBasedLighting(environment)

        return TiledPBRShader(renderable, environment, params)
    }
}