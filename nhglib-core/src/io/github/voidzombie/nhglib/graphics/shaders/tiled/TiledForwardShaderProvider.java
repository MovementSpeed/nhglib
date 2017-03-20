package io.github.voidzombie.nhglib.graphics.shaders.tiled;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * Created by Fausto Napoli on 20/03/2017.
 */
public class TiledForwardShaderProvider extends BaseShaderProvider {
    private Environment environment;

    public TiledForwardShaderProvider(Environment environment) {
        if (environment == null) throw new GdxRuntimeException("Environment parameter should not be null.");
        this.environment = environment;
    }

    @Override
    protected Shader createShader(Renderable renderable) {
        return new TiledForwardShader(renderable, environment);
    }
}
