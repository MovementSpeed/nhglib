package io.github.movementspeed.nhglib.physics.enums

import com.badlogic.gdx.physics.bullet.collision.Collision

/**
 * Created by Fausto Napoli on 16/05/2017.
 */
enum class ActivationState(var state: Int) {
    DISABLE_DEACTIVATION(Collision.DISABLE_DEACTIVATION),
    WANTS_DEACTIVATION(Collision.WANTS_DEACTIVATION);

    companion object {
        fun fromString(value: String): ActivationState {
            var state = WANTS_DEACTIVATION

            when (value) {
                "wantsDeactivation" -> state = WANTS_DEACTIVATION
                "disableDeactivation" -> state = DISABLE_DEACTIVATION
            }

            return state
        }
    }
}
