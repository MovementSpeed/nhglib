package io.github.movementspeed.nhglib.core.ecs.components.graphics

import com.artemis.Component
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import io.github.movementspeed.nhglib.assets.Asset
import io.github.movementspeed.nhglib.core.ecs.utils.UiManager
import io.github.movementspeed.nhglib.input.handler.InputProxy

class UiComponent : Component() {
    var fileName: String? = null
    var state: State
    var type: Type
    var uiManager: UiManager

    var actorNames: Array<String>
    var dependencies: Array<Asset>

    init {
        state = State.NOT_INITIALIZED
        type = Type.SCREEN
        dependencies = Array()
        actorNames = Array()
    }

    fun build(inputProxy: InputProxy, supportedRes: List<Vector2>) {
        uiManager = UiManager(fileName, supportedRes)
        uiManager.init(
                Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(),
                Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat(),
                dependencies)

        inputProxy.virtualInputHandler.addStage(fileName, uiManager.stage)

        state = State.READY
    }

    enum class State {
        NOT_INITIALIZED,
        READY
    }

    enum class Type {
        SCREEN,
        PANEL
    }
}
