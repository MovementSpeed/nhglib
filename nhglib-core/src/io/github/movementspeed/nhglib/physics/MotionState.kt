package io.github.movementspeed.nhglib.physics

import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState

/**
 * Created by Fausto Napoli on 04/05/2017.
 */
class MotionState : btMotionState() {
    var transform = Matrix4()

    override fun getWorldTransform(worldTrans: Matrix4) {
        worldTrans.set(transform)
    }

    override fun setWorldTransform(worldTrans: Matrix4) {
        transform.set(worldTrans)
    }
}
