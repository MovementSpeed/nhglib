package io.github.movementspeed.nhglib.runtime.ecs.interfaces;

import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.utils.Array;

public interface RenderingSystemInterface {
    void onPreRender();

    void onRender();

    void onPostRender();
    void clearRenderableProviders();

    void onUpdatedRenderer(int renderingWidth, int renderingHeight);
    Array<RenderableProvider> getRenderableProviders();
}