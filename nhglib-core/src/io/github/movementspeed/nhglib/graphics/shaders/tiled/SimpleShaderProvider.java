package io.github.movementspeed.nhglib.graphics.shaders.tiled;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider;
import io.github.movementspeed.nhglib.utils.graphics.ShaderUtils;

/**
 * Created by Fausto Napoli on 20/03/2017.
 */
public class SimpleShaderProvider extends BaseShaderProvider {
    @Override
    protected Shader createShader(Renderable renderable) {
        SimpleShader.Params params = new SimpleShader.Params();
        params.useBones = ShaderUtils.useBones(renderable);

        return new SimpleShader(renderable, params);
    }
}