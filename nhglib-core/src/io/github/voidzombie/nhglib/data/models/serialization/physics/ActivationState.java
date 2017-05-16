package io.github.voidzombie.nhglib.data.models.serialization.physics;

import com.badlogic.gdx.physics.bullet.collision.Collision;

/**
 * Created by Fausto Napoli on 16/05/2017.
 */
public enum ActivationState {
    DISABLE_DEACTIVATION(Collision.DISABLE_DEACTIVATION),
    WANTS_DEACTIVATION(Collision.WANTS_DEACTIVATION);

    public int state;

    ActivationState(int state) {
        this.state = state;
    }

    public static ActivationState fromString(String value) {
        ActivationState state = WANTS_DEACTIVATION;

        switch (value) {
            case "wantsDeactivation":
                state = WANTS_DEACTIVATION;
                break;

            case "disableDeactivation":
                state = DISABLE_DEACTIVATION;
                break;
        }

        return state;
    }
}
