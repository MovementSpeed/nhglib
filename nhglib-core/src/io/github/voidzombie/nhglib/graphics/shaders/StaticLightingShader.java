package io.github.voidzombie.nhglib.graphics.shaders;

import com.badlogic.gdx.graphics.g3d.Renderable;
import io.github.voidzombie.nhglib.utils.graphics.ShaderUtils;

/**
 * Created by Fausto Napoli on 13/03/2017.
 */
public class StaticLightingShader extends LightingShader {
    public StaticLightingShader(Renderable renderable) {
        super(renderable, false);
    }

    @Override
    public boolean canRender(Renderable instance) {
        return !ShaderUtils.isRenderableSkinned(instance);
    }
}