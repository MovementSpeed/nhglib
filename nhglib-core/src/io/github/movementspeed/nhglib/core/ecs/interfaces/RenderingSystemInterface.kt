package io.github.movementspeed.nhglib.core.ecs.interfaces;

import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.utils.Array;

public interface RenderingSystemInterface {
    void onPreRender();

    void onPostRender();

    void clearRenderableProviders();

    void onUpdatedRenderer(int renderingWidth, int renderingHeight);

    Array<RenderableProvider> getRenderableProviders();
}