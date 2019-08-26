package io.github.movementspeed.nhglib.core.ecs.systems.impl

import com.artemis.Aspect
import com.artemis.ComponentMapper
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import io.github.movementspeed.nhglib.core.ecs.components.graphics.ModelComponent
import io.github.movementspeed.nhglib.core.ecs.components.graphics.UiComponent
import io.github.movementspeed.nhglib.core.ecs.systems.base.BaseRenderingSystem
import io.github.movementspeed.nhglib.core.ecs.utils.Entities
import io.github.movementspeed.nhglib.graphics.shaders.attributes.PBRTextureAttribute

import java.util.ArrayList

class UiSystem(entities: Entities) : BaseRenderingSystem(Aspect.all(UiComponent::class.java), entities) {
    private val inputSystem: InputSystem? = null
    private val uiMapper: ComponentMapper<UiComponent>? = null
    private val modelMapper: ComponentMapper<ModelComponent>? = null

    private val supportedRes: MutableList<Vector2>
    private val uiComponents: Array<UiComponent>

    init {

        supportedRes = ArrayList()
        supportedRes.add(Vector2(1280f, 720f))
        supportedRes.add(Vector2(1920f, 1080f))

        uiComponents = Array()
    }

    override fun onPostRender() {
        super.onPostRender()
        for (uiComponent in uiComponents) {
            if (uiComponent.type == UiComponent.Type.SCREEN) {
                uiComponent.uiManager.renderUi(Gdx.graphics.deltaTime)
            }
        }

        uiComponents.clear()
    }

    override fun onUpdatedRenderer(renderingWidth: Int, renderingHeight: Int) {
        super.onUpdatedRenderer(renderingWidth, renderingHeight)

        for (uiComponent in uiComponents) {
            uiComponent.uiManager.resize(renderingWidth, renderingHeight)
        }
    }

    override fun process(entityId: Int) {
        val uiComponent = uiMapper!!.get(entityId)

        when (uiComponent.state) {
            UiComponent.State.READY -> when (uiComponent.type) {
                UiComponent.Type.SCREEN -> uiComponents.add(uiComponent)

                UiComponent.Type.PANEL -> {
                    // Currently not used, not completely implemented.
                    val modelComponent = modelMapper!!.get(entityId)

                    if (modelComponent != null && modelComponent.state == ModelComponent.State.READY) {
                        val texture = uiComponent.uiManager.renderUiToTexture(Gdx.graphics.deltaTime)
                        val textureAttribute = modelComponent.model.materials
                                .first().get(PBRTextureAttribute.Albedo) as PBRTextureAttribute

                        textureAttribute.set(texture)
                    }
                }
            }

            UiComponent.State.NOT_INITIALIZED -> if (inputSystem!!.inputProxy != null) {
                uiComponent.build(inputSystem.inputProxy!!, supportedRes)
            }
        }
    }
}
