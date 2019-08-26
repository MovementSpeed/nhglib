package io.github.movementspeed.nhglib

import io.github.movementspeed.nhglib.assets.Assets
import io.github.movementspeed.nhglib.core.ecs.utils.Entities
import io.github.movementspeed.nhglib.core.messaging.Messaging
import io.github.movementspeed.nhglib.core.threading.Threading
import io.github.movementspeed.nhglib.enums.OpenGLVersion

/**
 * Created by Fausto Napoli on 17/10/2016.
 * Entry point for Nhg, where various parts of the engine will be exposed.
 */
class Nhg {
    var assets = Assets()
    var messaging = Messaging()
    var threading = Threading()
    var entities = Entities()

    fun init() {
        assets.init(this)
    }

    companion object {
        var debugLogs = false
        var debugDrawPhysics = false
        var debugFpsLogs = false
        var glVersion = OpenGLVersion.VERSION_3
    }
}