package io.github.movementspeed.nhglib.runtime.ecs.interfaces;

import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.utils.Array;

public interface RenderingSystemInterface {
    Array<RenderableProvider> getRenderableProviders();
}
