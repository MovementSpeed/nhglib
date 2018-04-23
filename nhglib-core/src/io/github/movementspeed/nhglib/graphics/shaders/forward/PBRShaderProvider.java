package io.github.movementspeed.nhglib.graphics.shaders.forward;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider;
import com.badlogic.gdx.utils.GdxRuntimeException;
import io.github.movementspeed.nhglib.utils.graphics.ShaderUtils;

/**
 * Created by Fausto Napoli on 20/03/2017.
 */
public class PBRShaderProvider extends BaseShaderProvider {
    private Environment environment;

    public PBRShaderProvider(Environment environment) {
        if (environment == null) throw new GdxRuntimeException("Environment parameter should not be null.");
        this.environment = environment;
    }

    @Override
    protected Shader createShader(Renderable renderable) {
        PBRShader.Params params = new PBRShader.Params();
        params.albedo = ShaderUtils.hasAlbedo(renderable);
        params.metalness = ShaderUtils.hasMetalness(renderable);
        params.roughness = ShaderUtils.hasRoughness(renderable);
        params.normal = ShaderUtils.hasPbrNormal(renderable);
        params.ambientOcclusion = ShaderUtils.hasAmbientOcclusion(renderable);
        params.useBones = ShaderUtils.useBones(renderable);
        params.lit = ShaderUtils.hasLights(environment);
        params.gammaCorrection = ShaderUtils.useGammaCorrection(environment);
        params.imageBasedLighting = ShaderUtils.useImageBasedLighting(environment);

        return new PBRShader(renderable, environment, params);
    }
}