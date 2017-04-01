package io.github.voidzombie.nhglib.data.models.serialization.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.graphics.g3d.environment.SpotLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.Nhg;
import io.github.voidzombie.nhglib.data.models.serialization.ComponentJson;
import io.github.voidzombie.nhglib.enums.LightType;
import io.github.voidzombie.nhglib.runtime.ecs.components.graphics.LightComponent;
import io.github.voidzombie.nhglib.runtime.ecs.systems.impl.GraphicsSystem;

/**
 * Created by Fausto Napoli on 19/12/2016.
 */
public class LightComponentJson extends ComponentJson {
    @Override
    public void parse(JsonValue jsonValue) {
        GraphicsSystem graphicsSystem = Nhg.entitySystem.getEntitySystem(GraphicsSystem.class);

        LightComponent lightComponent =
                Nhg.entitySystem.createComponent(entity, LightComponent.class);

        BaseLight light = null;
        LightType lightType = LightType.fromString(jsonValue.getString("lightType"));

        float intensity = jsonValue.getFloat("intensity");

        JsonValue colorJson = jsonValue.get("color");
        Color color = new Color(colorJson.getFloat("r"), colorJson.getFloat("g"), colorJson.getFloat("b"), colorJson.getFloat("a"));

        switch (lightType) {
            case AMBIENT_LIGHT:
                break;

            case DIRECTIONAL_LIGHT:
                //light = new NhgDirectionalLight().set(color, Vector3.Z, intensity);
                break;

            case POINT_LIGHT:
                /*Float radius = jsonValue.getFloat("radius");
                light = new NhgPointLight().set(color, Vector3.Zero, intensity, radius);*/
                break;

            case SPOT_LIGHT:
                Float cutoffAngle = jsonValue.getFloat("cutoffAngle");
                Float exponent = jsonValue.getFloat("exponent");
                light = new SpotLight().set(color, Vector3.Zero, Vector3.Z, intensity, cutoffAngle, exponent);
                break;
        }

        if (light != null) {
            graphicsSystem.getEnvironment().add(light);
        }

        lightComponent.light = light;
        lightComponent.type = lightType;
        output = lightComponent;
    }
}
