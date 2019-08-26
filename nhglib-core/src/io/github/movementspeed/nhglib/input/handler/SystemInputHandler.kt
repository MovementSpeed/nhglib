package io.github.movementspeed.nhglib.input.handler

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.IntMap
import io.github.movementspeed.nhglib.input.enums.InputAction
import io.github.movementspeed.nhglib.input.enums.InputMode
import io.github.movementspeed.nhglib.input.enums.InputType
import io.github.movementspeed.nhglib.input.enums.TouchInputType
import io.github.movementspeed.nhglib.input.interfaces.InputHandler
import io.github.movementspeed.nhglib.input.models.base.NhgInput
import io.github.movementspeed.nhglib.input.models.impls.system.NhgKeyboardButtonInput
import io.github.movementspeed.nhglib.input.models.impls.system.NhgMouseButtonInput
import io.github.movementspeed.nhglib.input.models.impls.system.NhgTouchInput

class SystemInputHandler(private val inputProxy: InputProxy,
                         inputMultiplexer: InputMultiplexer,
                         systemInputArray: Array<NhgInput>) : InputHandler {
    private val vec0 = Vector2()

    private val activeKeyboardButtonInputs = Array<Int>()
    private val activeMouseButtonInputs = Array<Int>()
    private val activeTouchInputs = Array<Int>()

    private val keyboardButtonInputs = IntMap<NhgKeyboardButtonInput>()
    private val mouseButtonInputs = IntMap<NhgMouseButtonInput>()
    private val touchInputs = IntMap<NhgTouchInput>()

    private val isDesktop: Boolean
        get() = Gdx.app.type == Application.ApplicationType.Desktop

    init {
        mapSystemInput(systemInputArray)
        handleSystemInput(inputMultiplexer)
    }

    override fun update() {
        for (pointer in activeTouchInputs) {
            inputProxy.onInput(touchInputs.get(pointer!!))
        }

        for (keyCode in activeKeyboardButtonInputs) {
            inputProxy.onInput(keyboardButtonInputs.get(keyCode!!))
        }

        for (button in activeMouseButtonInputs) {
            inputProxy.onInput(mouseButtonInputs.get(button!!))
        }
    }

    private fun mapSystemInput(systemInputArray: Array<NhgInput>) {
        systemInputArray.forEach { nhgInput ->
            when (nhgInput.type) {
                InputType.KEYBOARD_BUTTON -> {
                    val keyboardButtonInput = nhgInput as NhgKeyboardButtonInput
                    val keyCode = keyboardButtonInput.keyCode
                    keyboardButtonInputs.put(keyCode, keyboardButtonInput)
                }

                InputType.MOUSE_BUTTON -> {
                    val mouseButtonInput = nhgInput as NhgMouseButtonInput
                    val buttonCode = mouseButtonInput.buttonCode
                    mouseButtonInputs.put(buttonCode, mouseButtonInput)
                }

                InputType.TOUCH -> {
                    val touchInput = nhgInput as NhgTouchInput
                    val pointerNumber = touchInput.pointerNumber
                    touchInputs.put(pointerNumber, touchInput)
                }

                InputType.CONTROLLER_BUTTON -> TODO()
                InputType.VIRTUAL_BUTTON -> TODO()
                InputType.VIRTUAL_STICK -> TODO()
                InputType.CONTROLLER_STICK -> TODO()
                null -> TODO()
            }
        }
    }

    private fun handleSystemInput(inputMultiplexer: InputMultiplexer) {
        val highLevelInput = GestureDetector(object : GestureDetector.GestureListener {
            override fun touchDown(x: Float, y: Float, pointer: Int, button: Int): Boolean {
                // pass through to low level handler
                return false
            }

            override fun tap(x: Float, y: Float, count: Int, button: Int): Boolean {
                return false
            }

            override fun longPress(x: Float, y: Float): Boolean {
                return false
            }

            override fun fling(velocityX: Float, velocityY: Float, button: Int): Boolean {
                return false
            }

            override fun pan(x: Float, y: Float, deltaX: Float, deltaY: Float): Boolean {
                return false
            }

            override fun panStop(x: Float, y: Float, pointer: Int, button: Int): Boolean {
                return false
            }

            override fun zoom(initialDistance: Float, distance: Float): Boolean {
                val input0 = touchInputs.get(0)

                if (input0 != null && input0.isValid) {
                    if (input0.hasTouchInputType(TouchInputType.ZOOM)) {
                        val values = vec0.set(initialDistance, distance)

                        input0.action = InputAction.ZOOM
                        input0.value = values

                        inputProxy.onInput(input0)
                        return true
                    }
                }


                return false
            }

            override fun pinch(initialPointer1: Vector2, initialPointer2: Vector2, pointer1: Vector2, pointer2: Vector2): Boolean {
                val input0 = touchInputs.get(0)

                if (input0 != null && input0.isValid) {
                    if (input0.hasTouchInputType(TouchInputType.PINCH)) {
                        val dS = Vector2.dst(initialPointer1.x, initialPointer1.y, initialPointer2.x, initialPointer2.y)
                        val dF = Vector2.dst(pointer1.x, pointer1.y, pointer2.x, pointer2.y)
                        val value = dF - dS

                        input0.action = InputAction.PINCH
                        input0.value = value

                        inputProxy.onInput(input0)
                        return true
                    }
                }

                return false
            }

            override fun pinchStop() {

            }
        })

        val lowLevelInput = object : InputProcessor {
            override fun keyDown(keyCode: Int): Boolean {
                val input = keyboardButtonInputs.get(keyCode)

                if (input != null && input.isValid) {
                    input.action = InputAction.DOWN

                    when (input.mode) {
                        InputMode.REPEAT -> if (!activeKeyboardButtonInputs.contains(keyCode, true)) {
                            activeKeyboardButtonInputs.add(keyCode)
                        }

                        else -> inputProxy.onInput(input)
                    }
                }

                return false
            }

            override fun keyUp(keyCode: Int): Boolean {
                val input = keyboardButtonInputs.get(keyCode)

                if (input != null && input.isValid) {
                    input.action = InputAction.UP
                    activeKeyboardButtonInputs.removeValue(keyCode, true)
                    inputProxy.onInput(input)
                }

                return false
            }

            override fun keyTyped(c: Char): Boolean {
                return false
            }

            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                val input: NhgInput?

                if (isDesktop) {
                    input = mouseButtonInputs.get(button)

                    if (input != null && input.isValid) {
                        input.action = InputAction.DOWN
                        input.value = vec0.set(screenX.toFloat(), screenY.toFloat())

                        when (input.mode) {
                            InputMode.REPEAT -> if (!activeMouseButtonInputs.contains(button, true)) {
                                activeMouseButtonInputs.add(button)
                            }

                            else -> inputProxy.onInput(input)
                        }
                    }
                } else {
                    val touchInput = touchInputs.get(pointer)

                    if (touchInput != null && touchInput.isValid) {
                        if (touchInput.hasTouchInputType(TouchInputType.TAP)) {
                            touchInput.action = InputAction.DOWN
                            touchInput.value = vec0.set(screenX.toFloat(), screenY.toFloat())

                            when (touchInput.mode) {
                                InputMode.REPEAT -> if (!activeTouchInputs.contains(pointer, true)) {
                                    activeTouchInputs.add(pointer)
                                }

                                else -> inputProxy.onInput(touchInput)
                            }
                        }
                    }
                }

                return false
            }

            override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                val input: NhgInput?

                if (isDesktop) {
                    input = mouseButtonInputs.get(button)

                    if (input != null && input.isValid) {
                        input.action = InputAction.UP
                        input.value = vec0.set(screenX.toFloat(), screenY.toFloat())

                        activeMouseButtonInputs.removeValue(button, true)
                        inputProxy.onInput(input)
                    }
                } else {
                    val touchInput = touchInputs.get(pointer)

                    if (touchInput != null && touchInput.isValid) {
                        if (touchInput.hasTouchInputType(TouchInputType.TAP)) {
                            touchInput.action = InputAction.UP
                            touchInput.value = vec0.set(screenX.toFloat(), screenY.toFloat())

                            activeTouchInputs.removeValue(pointer, true)
                            inputProxy.onInput(touchInput)
                        }
                    }
                }

                return false
            }

            override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
                val screenXTemp: Int
                val screenYTemp: Int
                val input = touchInputs.get(pointer)

                if (input != null && input.isValid) {
                    if (input.hasTouchInputType(TouchInputType.DRAG)) {
                        screenXTemp = Gdx.input.getDeltaX(pointer)
                        screenYTemp = Gdx.input.getDeltaY(pointer)

                        input.action = InputAction.DRAG
                        input.value = vec0.set(screenXTemp.toFloat(), screenYTemp.toFloat())

                        inputProxy.onInput(input)
                    }
                }

                return false
            }

            override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
                return false
            }

            override fun scrolled(amount: Int): Boolean {
                return false
            }
        }

        inputMultiplexer.addProcessor(highLevelInput)
        inputMultiplexer.addProcessor(lowLevelInput)
    }
}
