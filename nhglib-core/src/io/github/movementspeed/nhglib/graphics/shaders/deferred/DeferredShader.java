package io.github.movementspeed.nhglib.graphics.shaders.deferred;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;

/**
 * Created by Fausto Napoli on 05/09/2017.
 */
public class DeferredShader extends BaseShader {
    @Override
    public void init() {

    }

    @Override
    public int compareTo(Shader other) {
        return 0;
    }

    @Override
    public boolean canRender(Renderable instance) {
        return false;
    }
}
