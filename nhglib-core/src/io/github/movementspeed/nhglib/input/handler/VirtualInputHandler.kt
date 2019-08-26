package io.github.movementspeed.nhglib.input.handler

import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ArrayMap
import com.badlogic.gdx.utils.ObjectMap
import io.github.movementspeed.nhglib.input.enums.InputAction
import io.github.movementspeed.nhglib.input.enums.InputMode
import io.github.movementspeed.nhglib.input.interfaces.InputHandler
import io.github.movementspeed.nhglib.input.models.base.NhgInput
import io.github.movementspeed.nhglib.input.models.impls.virtual.NhgVirtualButtonInput

class VirtualInputHandler(private val inputProxy: InputProxy,
                          private val inputMultiplexer: InputMultiplexer,
                          originalVirtualInputArray: Array<NhgInput>) : InputHandler {
    private val vec0 = Vector2()

    private val activeVirtualInputs = Array<String>()
    private val virtualInputs = ArrayMap<String, NhgVirtualButtonInput>()
    private val stages: ArrayMap<String, Stage> = ArrayMap()

    init {
        processVirtualInputArray(originalVirtualInputArray)
    }

    override fun update() {
        for (actorName in activeVirtualInputs) {
            inputProxy.onInput(virtualInputs.get(actorName))
        }
    }

    fun addStage(name: String, value: Stage) {
        stages.put(name, value)
        inputMultiplexer.addProcessor(value)
        processStages()
    }

    fun removeStage(name: String) {
        val stage = stages.get(name)
        inputMultiplexer.removeProcessor(stage)
        stages.removeKey(name)
        processStages()
    }

    private fun processVirtualInputArray(virtualInputArray: Array<NhgInput>) {
        for (input in virtualInputArray) {
            val virtualInput = input as NhgVirtualButtonInput
            val actorName = virtualInput.actorName
            virtualInputs.put(actorName, virtualInput)
        }
    }

    private fun processStages() {
        activeVirtualInputs.clear()

        for (stage in stages.values()) {
            val stageRoot = stage.root

            for (entry in virtualInputs.entries()) {
                val actorName = entry.key
                val actor = stageRoot.findActor<Actor>(actorName)

                if (actor != null) {
                    val virtualInput = entry.value

                    actor.clearListeners()
                    actor.addListener(object : ClickListener() {
                        override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                            if (virtualInput.isValid) {
                                virtualInput.action = InputAction.DOWN
                                virtualInput.value = vec0.set(x, y)

                                when (virtualInput.mode) {
                                    InputMode.REPEAT -> if (!activeVirtualInputs.contains(actorName, false)) {
                                        activeVirtualInputs.add(actorName)
                                    }

                                    else -> inputProxy.onInput(virtualInput)
                                }
                            }

                            return virtualInput.isValid
                        }

                        override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                            if (virtualInput.isValid) {
                                virtualInput.action = InputAction.UP
                                virtualInput.value = vec0.set(x, y)

                                when (virtualInput.mode) {
                                    InputMode.REPEAT -> activeVirtualInputs.removeValue(actorName, false)

                                    else -> inputProxy.onInput(virtualInput)
                                }
                            }
                        }
                    })
                }
            }
        }
    }
}
