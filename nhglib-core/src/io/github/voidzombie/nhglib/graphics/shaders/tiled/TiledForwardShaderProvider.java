package io.github.voidzombie.nhglib.graphics.shaders.tiled;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider;
import com.badlogic.gdx.utils.GdxRuntimeException;
import io.github.voidzombie.nhglib.utils.graphics.ShaderUtils;

/**
 * Created by Fausto Napoli on 20/03/2017.
 */
public class TiledForwardShaderProvider extends BaseShaderProvider {
    private String vertexShaderPath;
    private String fragmentShaderPath;
    private Environment environment;

    public TiledForwardShaderProvider(String vertexShaderPath, String fragmentShaderPath, Environment environment) {
        if (vertexShaderPath == null) throw new GdxRuntimeException("vertexShaderPath parameter should not be null.");
        if (fragmentShaderPath == null)
            throw new GdxRuntimeException("fragmentShaderPath parameter should not be null.");
        if (environment == null) throw new GdxRuntimeException("Environment parameter should not be null.");

        this.vertexShaderPath = vertexShaderPath;
        this.fragmentShaderPath = fragmentShaderPath;
        this.environment = environment;
    }

    @Override
    protected Shader createShader(Renderable renderable) {
        TiledForwardShader.Params params = new TiledForwardShader.Params();

        params.vertexShaderPath = vertexShaderPath;
        params.fragmentShaderPath = fragmentShaderPath;

        params.albedo = ShaderUtils.hasAlbedo(renderable);
        params.metalness = ShaderUtils.hasMetalness(renderable);
        params.roughness = ShaderUtils.hasRoughness(renderable);
        params.normal = ShaderUtils.hasNormal(renderable);
        params.ambientOcclusion = ShaderUtils.hasAmbientOcclusion(renderable);
        params.useBones = ShaderUtils.useBones(renderable);
        params.lit = ShaderUtils.hasLights(environment);

        return new TiledForwardShader(renderable, environment, params);
    }
}
