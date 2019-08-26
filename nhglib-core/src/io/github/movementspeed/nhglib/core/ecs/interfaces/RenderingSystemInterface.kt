package io.github.movementspeed.nhglib.core.ecs.interfaces

import com.badlogic.gdx.graphics.g3d.RenderableProvider
import com.badlogic.gdx.utils.Array

interface RenderingSystemInterface {

    val renderableProviders: Array<RenderableProvider>
    fun onPreRender()

    fun onPostRender()

    fun clearRenderableProviders()

    fun onUpdatedRenderer(renderingWidth: Int, renderingHeight: Int)
}