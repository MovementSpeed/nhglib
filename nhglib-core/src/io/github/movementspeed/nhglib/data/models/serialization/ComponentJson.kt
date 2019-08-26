package io.github.movementspeed.nhglib.data.models.serialization

import com.artemis.Component
import io.github.movementspeed.nhglib.Nhg
import io.github.movementspeed.nhglib.graphics.scenes.SceneGraph
import io.github.movementspeed.nhglib.interfaces.JsonParseable

/**
 * Created by Fausto Napoli on 19/12/2016.
 */
abstract class ComponentJson : JsonParseable<Component> {
    var parentEntity: Int = 0
    var entity: Int = 0

    var nhg: Nhg? = null
    var sceneGraph: SceneGraph? = null

    protected var output: Component? = null

    override fun get(): Component? {
        return output
    }
}
