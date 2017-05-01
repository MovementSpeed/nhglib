package io.github.voidzombie.nhglib.data.models.serialization.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.data.models.serialization.ComponentJson;
import io.github.voidzombie.nhglib.enums.LightType;
import io.github.voidzombie.nhglib.graphics.lights.NhgLight;
import io.github.voidzombie.nhglib.graphics.lights.NhgLightsAttribute;
import io.github.voidzombie.nhglib.runtime.ecs.components.graphics.LightComponent;
import io.github.voidzombie.nhglib.runtime.ecs.systems.impl.GraphicsSystem;

/**
 * Created by Fausto Napoli on 19/12/2016.
 */
public class LightComponentJson extends ComponentJson {
    @Override
    public void parse(JsonValue jsonValue) {
        GraphicsSystem graphicsSystem = entities.getEntitySystem(GraphicsSystem.class);
        LightComponent lightComponent = entities.createComponent(entity, LightComponent.class);

        LightType lightType = LightType.fromString(jsonValue.getString("lightType"));
        float range = jsonValue.getFloat("range", 1f);
        float intensity = jsonValue.getFloat("intensity", 1f);
        float innerAngle = jsonValue.getFloat("innerAngle", 0f);
        float outerAngle = jsonValue.getFloat("outerAngle", 0f);

        if (innerAngle > outerAngle) {
            innerAngle = outerAngle;
        }

        if (range < 1.0f) {
            range = 1.0f;
        }

        JsonValue colorJson = jsonValue.get("color");
        Color color = new Color(colorJson.getFloat("r"), colorJson.getFloat("g"), colorJson.getFloat("b"), colorJson.getFloat("a"));

        /*JsonValue directionJson = jsonValue.get("direction");
        Vector3 direction = VectorPool.getVector3();

        if (directionJson != null) {
            direction.set(
                    directionJson.getFloat("x"),
                    directionJson.getFloat("y"),
                    directionJson.getFloat("z"));
        }*/

        NhgLight light = null;

        switch (lightType) {
            case DIRECTIONAL_LIGHT:
                light = NhgLight.directional(intensity, color);
                break;

            case POINT_LIGHT:
                light = NhgLight.point(intensity, range, color);
                break;

            case SPOT_LIGHT:
                light = NhgLight.spot(intensity, range, innerAngle, outerAngle, color);
                break;
        }

        if (light == null) return;

        Environment environment = graphicsSystem.getEnvironment();
        NhgLightsAttribute attribute = (NhgLightsAttribute) environment
                .get(NhgLightsAttribute.Type);

        if (attribute == null) {
            attribute = new NhgLightsAttribute();
            environment.set(attribute);
        }

        attribute.lights.add(light);

        lightComponent.light = light;
        lightComponent.type = lightType;
        output = lightComponent;
    }
}
