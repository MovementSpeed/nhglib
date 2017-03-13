package io.github.voidzombie.nhglib.graphics.shaders;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import io.github.voidzombie.nhglib.utils.graphics.ShaderUtils;

/**
 * Created by worse on 13/03/2017.
 */
public class NhgShaderProvider extends DefaultShaderProvider {
    private Environment environment;

    public NhgShaderProvider(Environment environment) {
        this.environment = environment;
    }

    @Override
    protected Shader createShader(Renderable renderable) {
        boolean skinned = ShaderUtils.isRenderableSkinned(renderable);
        Shader shader;

        if (skinned) {
            shader = new SkinnedLightingShader(renderable);
        } else {
            shader = new StaticLightingShader(renderable);
        }

        return shader;
    }

    public Environment getEnvironment() {
        return environment;
    }
}
