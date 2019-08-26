package io.github.movementspeed.nhglib.core.ecs.components.graphics

import com.artemis.PooledComponent
import io.github.movementspeed.nhglib.enums.LightType
import io.github.movementspeed.nhglib.graphics.lights.NhgLight

/**
 * Created by Fausto Napoli on 14/03/2017.
 */
class LightComponent : PooledComponent() {
    var light: NhgLight? = null
    var type: LightType? = null

    override fun reset() {
        light = null
        type = null
    }
}
