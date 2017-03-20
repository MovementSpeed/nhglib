package io.github.voidzombie.nhglib.graphics.shaders.forward;

import com.badlogic.gdx.graphics.g3d.Renderable;
import io.github.voidzombie.nhglib.utils.graphics.ShaderUtils;

/**
 * Created by Fausto Napoli on 13/03/2017.
 */
public class SkinnedLightingShader extends LightingShader {
    public SkinnedLightingShader(Renderable renderable) {
        super(renderable, true);
    }

    @Override
    public boolean canRender(Renderable instance) {
        return ShaderUtils.isRenderableSkinned(instance);
    }
}