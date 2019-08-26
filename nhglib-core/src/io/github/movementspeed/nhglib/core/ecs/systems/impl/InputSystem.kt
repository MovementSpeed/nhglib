package io.github.movementspeed.nhglib.core.ecs.systems.impl

import com.artemis.BaseSystem
import com.badlogic.gdx.utils.Array
import io.github.movementspeed.nhglib.Nhg
import io.github.movementspeed.nhglib.assets.Asset
import io.github.movementspeed.nhglib.assets.Assets
import io.github.movementspeed.nhglib.core.messaging.Message
import io.github.movementspeed.nhglib.input.handler.InputProxy
import io.github.movementspeed.nhglib.input.interfaces.InputListener
import io.github.movementspeed.nhglib.input.models.base.NhgInput
import io.github.movementspeed.nhglib.utils.data.Strings
import io.github.movementspeed.nhglib.utils.debug.NhgLogger
import io.reactivex.functions.Consumer

class InputSystem(private val assets: Assets) : BaseSystem(), InputListener {
    var inputProxy: InputProxy? = null
        private set
    private val inputListeners: Array<InputListener>

    init {
        inputListeners = Array()
    }

    override fun onInput(input: NhgInput) {
        for (inputListener in inputListeners) {
            inputListener.onInput(input)
        }
    }

    override fun processSystem() {
        if (inputProxy != null) {
            inputProxy!!.update()
        }
    }

    fun addInputListener(inputListener: InputListener) {
        inputListeners.add(inputListener)
    }

    fun loadMapping(fileName: String) {
        inputProxy = assets.loadAssetSync<InputProxy>(Asset("nhgInputMap", fileName, InputProxy::class.java))
        inputProxy!!.setInputListener(this)
    }

    fun setEnableContext(name: String, enable: Boolean) {
        if (inputProxy != null) {
            inputProxy!!.setEnableContext(name, enable)
        } else {
            NhgLogger.log(this, "Can't enable context \"%s\", first load an input mapping json.")
        }
    }
}
