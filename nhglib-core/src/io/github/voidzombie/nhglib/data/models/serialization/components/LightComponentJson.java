package io.github.voidzombie.nhglib.data.models.serialization.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.data.models.serialization.ComponentJson;
import io.github.voidzombie.nhglib.enums.LightType;
import io.github.voidzombie.nhglib.graphics.lights.NhgLight;
import io.github.voidzombie.nhglib.graphics.lights.NhgLightsAttribute;
import io.github.voidzombie.nhglib.runtime.ecs.components.graphics.LightComponent;
import io.github.voidzombie.nhglib.runtime.ecs.systems.impl.GraphicsSystem;
import io.github.voidzombie.nhglib.utils.data.VectorPool;

/**
 * Created by Fausto Napoli on 19/12/2016.
 */
public class LightComponentJson extends ComponentJson {
    @Override
    public void parse(JsonValue jsonValue) {
        GraphicsSystem graphicsSystem = entities.getEntitySystem(GraphicsSystem.class);

        LightComponent lightComponent = entities.createComponent(entity, LightComponent.class);

        NhgLight light = new NhgLight();

        LightType lightType = LightType.fromString(jsonValue.getString("lightType"));
        float radius = jsonValue.getFloat("radius");
        float intensity = jsonValue.getFloat("intensity");
        float innerAngle = jsonValue.getFloat("innerAngle");
        float outerAngle = jsonValue.getFloat("outerAngle");

        JsonValue colorJson = jsonValue.get("color");
        Color color = new Color(colorJson.getFloat("r"), colorJson.getFloat("g"), colorJson.getFloat("b"), colorJson.getFloat("a"));

        JsonValue directionJson = jsonValue.get("direction");
        Vector3 direction = VectorPool.getVector3();

        if (directionJson != null) {
            direction.set(
                    directionJson.getFloat("x"),
                    directionJson.getFloat("y"),
                    directionJson.getFloat("z"));
        }

        light.color.set(color);
        light.direction.set(direction);
        light.radius = radius;
        light.intensity = intensity;
        light.type = lightType.ordinal();

        if (lightType == LightType.SPOT_LIGHT) {
            light.innerAngle = innerAngle;
            light.outerAngle = outerAngle;
        }

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
