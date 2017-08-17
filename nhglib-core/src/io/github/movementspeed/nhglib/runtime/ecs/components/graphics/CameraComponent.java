package io.github.movementspeed.nhglib.runtime.ecs.components.graphics;

import com.artemis.Component;
import com.badlogic.gdx.graphics.Camera;

/**
 * Created by Fausto Napoli on 03/03/2017.
 */
public class CameraComponent extends Component {
    public Camera camera;
    public Type type;

    public enum Type {
        PERSPECTIVE,
        ORTHOGRAPHIC;

        public static CameraComponent.Type fromString(String value) {
            CameraComponent.Type type = null;

            if (value.contentEquals("perspective")) {
                type = PERSPECTIVE;
            } else if (value.contentEquals("orthographic")) {
                type = ORTHOGRAPHIC;
            }

            return type;
        }
    }
}
