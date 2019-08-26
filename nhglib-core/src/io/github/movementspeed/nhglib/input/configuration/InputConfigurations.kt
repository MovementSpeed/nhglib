package io.github.movementspeed.nhglib.input.configuration

import com.badlogic.gdx.utils.Array
import io.github.movementspeed.nhglib.input.configuration.impls.KeyInputConfiguration
import io.github.movementspeed.nhglib.input.configuration.impls.MouseInputConfiguration
import io.github.movementspeed.nhglib.input.configuration.impls.PointerInputConfiguration
import io.github.movementspeed.nhglib.input.configuration.impls.StickInputConfiguration
import io.github.movementspeed.nhglib.input.controllers.ControllerConfiguration
import io.github.movementspeed.nhglib.input.enums.MouseSourceType

/**
 * Created by Fausto Napoli on 25/01/2017.
 */
class InputConfigurations {
    val keyInputConfigurations = Array<KeyInputConfiguration>()
    val pointerInputConfigurations = Array<PointerInputConfiguration>()
    val stickInputConfigurations = Array<StickInputConfiguration>()
    val controllerConfigurations = Array<ControllerConfiguration>()
    val mouseInputConfigurations = Array<MouseInputConfiguration>()

    fun getKeyConfiguration(name: String): KeyInputConfiguration? {
        var res: KeyInputConfiguration? = null

        for (configuration in keyInputConfigurations) {
            if (configuration.name.contentEquals(name)) {
                res = configuration
            }
        }

        return res
    }

    fun getPointerConfiguration(name: String): PointerInputConfiguration? {
        var res: PointerInputConfiguration? = null

        for (configuration in pointerInputConfigurations) {
            if (configuration.name.contentEquals(name)) {
                res = configuration
            }
        }

        return res
    }

    fun getStickConfiguration(name: String): StickInputConfiguration? {
        var res: StickInputConfiguration? = null

        for (configuration in stickInputConfigurations) {
            if (configuration.name.contentEquals(name)) {
                res = configuration
            }
        }

        return res
    }

    fun getMouseConfiguration(name: String): MouseInputConfiguration? {
        var res: MouseInputConfiguration? = null

        for (configuration in mouseInputConfigurations) {
            if (configuration.name.contentEquals(name)) {
                res = configuration
            }
        }

        return res
    }

    fun getMouseConfiguration(sourceType: MouseSourceType): MouseInputConfiguration? {
        var res: MouseInputConfiguration? = null

        for (configuration in mouseInputConfigurations) {
            if (configuration.mouseSourceType == sourceType) {
                res = configuration
            }
        }

        return res
    }

    fun getControllerConfiguration(id: Int): ControllerConfiguration? {
        var res: ControllerConfiguration? = null

        for (configuration in controllerConfigurations) {
            if (configuration.id == id) {
                res = configuration
            }
        }

        return res
    }
}
