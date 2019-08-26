package io.github.movementspeed.nhglib.input.handler

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Predicate
import io.github.movementspeed.nhglib.input.interfaces.InputHandler
import io.github.movementspeed.nhglib.input.interfaces.InputListener
import io.github.movementspeed.nhglib.input.models.InputContext
import io.github.movementspeed.nhglib.input.models.base.NhgInput
import io.github.movementspeed.nhglib.input.models.impls.virtual.NhgVirtualButtonInput
import io.github.movementspeed.nhglib.interfaces.Updatable

class InputProxy : Updatable {
    var inputAction: (input: NhgInput) -> Unit = {}

    private val inputMultiplexer = InputMultiplexer()
    private var systemInputHandler: InputHandler? = null
    private var virtualInputHandler: VirtualInputHandler? = null

    private var inputContexts: Array<InputContext>? = null

    init {
        Gdx.input.inputProcessor = inputMultiplexer
    }

    override fun update() {
        virtualInputHandler?.update()
        systemInputHandler?.update()
    }

    fun setEnableContext(name: String, enable: Boolean) {
        inputContexts?.forEach {
            if (it.`is`(name)) {
                it.isEnabled = enable
            }
        }
    }

    fun build(inputContexts: Array<InputContext>, systemInputArray: Array<NhgInput>, virtualInputArray: Array<NhgInput>) {
        this.inputContexts = inputContexts

        virtualInputHandler = VirtualInputHandler(
                this, inputMultiplexer, virtualInputArray)

        systemInputHandler = SystemInputHandler(
                this, inputMultiplexer, systemInputArray)
    }
}
