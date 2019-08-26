package io.github.movementspeed.nhglib.assets.loaders

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonValue
import io.github.movementspeed.nhglib.input.enums.InputMode
import io.github.movementspeed.nhglib.input.enums.InputType
import io.github.movementspeed.nhglib.input.enums.TouchInputType
import io.github.movementspeed.nhglib.input.handler.InputProxy
import io.github.movementspeed.nhglib.input.models.InputContext
import io.github.movementspeed.nhglib.input.models.base.NhgInput
import io.github.movementspeed.nhglib.input.models.impls.system.NhgKeyboardButtonInput
import io.github.movementspeed.nhglib.input.models.impls.system.NhgTouchInput
import io.github.movementspeed.nhglib.input.models.impls.virtual.NhgVirtualButtonInput
import io.github.movementspeed.nhglib.input.utils.InputUtil
import io.github.movementspeed.nhglib.utils.debug.NhgLogger

class InputLoader(resolver: FileHandleResolver) : AsynchronousAssetLoader<InputProxy, InputLoader.InputProxyParameter>(resolver) {

    override fun loadAsync(assetManager: AssetManager, s: String, fileHandle: FileHandle, inputProxyParameter: InputProxyParameter) {

    }

    override fun loadSync(assetManager: AssetManager, s: String, fileHandle: FileHandle, inputProxyParameter: InputProxyParameter): InputProxy {
        val inputProxy = InputProxy()
        val jsonValue = JsonReader().parse(fileHandle)
        val inputContextsJsonArray = jsonValue.get("inputContexts")

        val systemInputs = Array<NhgInput>()
        val virtualInputs = Array<NhgInput>()
        val inputContexts = Array<InputContext>()

        for (inputContextJson in inputContextsJsonArray) {
            val inputContextName = inputContextJson.getString("name")
            val inputsJsonArray = inputContextJson.get("inputs")

            val inputContext = InputContext(inputContextName)
            inputContexts.add(inputContext)

            for (inputJson in inputsJsonArray) {
                var nhgInput: NhgInput? = null

                val inputName = inputJson.getString("inputName")
                val inputType = InputType.fromString(inputJson.getString("inputType"))
                val inputMode = InputMode.fromString(inputJson.getString("inputMode"))

                when (inputType) {
                    InputType.TOUCH -> {
                        val pointerNumber = inputJson.getInt("pointerNumber")
                        val touchInputTypes = Array<TouchInputType>()

                        if (inputJson.has("touchInputTypes")) {
                            val inputTypes = inputJson.getString("touchInputTypes")
                            val inputTypesArray = inputTypes.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                            for (touchInputType in inputTypesArray) {
                                touchInputTypes.add(TouchInputType.fromString(touchInputType))
                            }
                        }

                        if (touchInputTypes.size == 0) {
                            val touchInputType = TouchInputType.TAP
                            touchInputTypes.add(touchInputType)
                        }

                        val touchInput = NhgTouchInput(inputName)
                        touchInput.pointerNumber = pointerNumber
                        touchInput.setTouchInputTypes(touchInputTypes)

                        nhgInput = touchInput
                        systemInputs.add(nhgInput)
                    }

                    InputType.MOUSE_BUTTON -> {
                    }

                    InputType.KEYBOARD_BUTTON -> {
                        val key = inputJson.getString("key").toUpperCase()

                        val keyboardButtonInput = NhgKeyboardButtonInput(inputName)
                        keyboardButtonInput.keyCode = InputUtil.keyCodeFromName(key)
                        nhgInput = keyboardButtonInput
                        systemInputs.add(nhgInput)
                    }

                    InputType.VIRTUAL_BUTTON -> {
                        val actorName = inputJson.getString("actorName")

                        val virtualButtonInput = NhgVirtualButtonInput(inputName)
                        virtualButtonInput.actorName = actorName
                        nhgInput = virtualButtonInput
                        virtualInputs.add(nhgInput)
                    }
                }

                if (nhgInput != null) {
                    nhgInput.mode = inputMode
                    nhgInput.context = inputContext
                } else {
                    NhgLogger.log("InputLoader", "ignored input \"%s\"", inputName)
                }
            }
        }

        inputProxy.build(inputContexts, systemInputs, virtualInputs)
        return inputProxy
    }

    override fun getDependencies(s: String, fileHandle: FileHandle, inputProxyParameter: InputProxyParameter): Array<AssetDescriptor<*>>? {
        return null
    }

    class InputProxyParameter : AssetLoaderParameters<InputProxy>()
}
