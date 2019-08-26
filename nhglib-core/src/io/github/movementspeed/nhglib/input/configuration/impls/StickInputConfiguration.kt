package io.github.movementspeed.nhglib.input.configuration.impls

import io.github.movementspeed.nhglib.input.configuration.base.InputConfiguration
import io.github.movementspeed.nhglib.input.enums.StickType

/**
 * Created by Fausto Napoli on 24/01/2017.
 */
class StickInputConfiguration : InputConfiguration() {
    var controllerId: Int = 0
    var stickType: StickType? = null
}
