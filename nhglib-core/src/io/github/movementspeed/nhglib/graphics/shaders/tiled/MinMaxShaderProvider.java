package io.github.movementspeed.nhglib.graphics.shaders.tiled;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider;
import io.github.movementspeed.nhglib.utils.graphics.ShaderUtils;

/**
 * Created by Fausto Napoli on 20/03/2017.
 */
public class MinMaxShaderProvider extends BaseShaderProvider {
    public Texture depthTexture;

    @Override
    protected Shader createShader(Renderable renderable) {
        MinMaxShader.Params params = new MinMaxShader.Params();
        params.useBones = ShaderUtils.useBones(renderable);
        return new MinMaxShader(renderable, depthTexture, params);
    }

    public void setDepthTexture(Texture texture) {
        depthTexture = texture;
    }
}
