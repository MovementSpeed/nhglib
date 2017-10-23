package io.github.movementspeed.nhglib.data.models.serialization.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.utils.JsonValue;
import io.github.movementspeed.nhglib.core.ecs.components.graphics.CameraComponent;
import io.github.movementspeed.nhglib.data.models.serialization.ComponentJson;

/**
 * Created by Fausto Napoli on 19/12/2016.
 */
public class CameraComponentJson extends ComponentJson {
    @Override
    public void parse(JsonValue jsonValue) {
        CameraComponent cameraComponent =
                nhg.entities.createComponent(entity, CameraComponent.class);

        Camera camera;

        float nearPlane = jsonValue.getFloat("nearPlane");
        float farPlane = jsonValue.getFloat("farPlane");

        CameraComponent.Type type = CameraComponent.Type.fromString(
                jsonValue.getString("cameraType"));

        switch (type) {
            default:
            case PERSPECTIVE:
                float fieldOfView = jsonValue.getFloat("fieldOfView");

                camera = new PerspectiveCamera(
                        fieldOfView,
                        Gdx.graphics.getWidth(),
                        Gdx.graphics.getHeight());
                break;

            case ORTHOGRAPHIC:
                camera = new OrthographicCamera();
                break;
        }

        camera.near = nearPlane;
        camera.far = farPlane;

        cameraComponent.camera = camera;
        cameraComponent.type = type;

        output = cameraComponent;
    }
}
