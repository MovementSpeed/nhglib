package io.github.movementspeed.nhglib.graphics.shaders.tiled;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider;
import com.badlogic.gdx.utils.GdxRuntimeException;
import io.github.movementspeed.nhglib.graphics.lighting.tiled.LightGrid;
import io.github.movementspeed.nhglib.utils.graphics.ShaderUtils;

/**
 * Created by Fausto Napoli on 20/03/2017.
 */
public class TiledPBRShaderProvider extends BaseShaderProvider {
    private Environment environment;
    private LightGrid lightGrid;

    public TiledPBRShaderProvider(Environment environment) {
        if (environment == null) throw new GdxRuntimeException("Environment parameter should not be null.");
        this.environment = environment;
    }

    @Override
    protected Shader createShader(Renderable renderable) {
        TiledPBRShader.Params params = new TiledPBRShader.Params();
        params.albedo = ShaderUtils.hasAlbedo(renderable);
        params.metalness = ShaderUtils.hasMetalness(renderable);
        params.roughness = ShaderUtils.hasRoughness(renderable);
        params.normal = ShaderUtils.hasPbrNormal(renderable);
        params.ambientOcclusion = ShaderUtils.hasAmbientOcclusion(renderable);
        params.useBones = ShaderUtils.useBones(renderable);
        params.lit = ShaderUtils.hasLights(environment);
        params.gammaCorrection = ShaderUtils.useGammaCorrection(environment);
        params.imageBasedLighting = ShaderUtils.useImageBasedLighting(environment);

        return new TiledPBRShader(renderable, environment, lightGrid, params);
    }

    public void setLightGrid(LightGrid lightGrid) {
        this.lightGrid = lightGrid;
    }
}