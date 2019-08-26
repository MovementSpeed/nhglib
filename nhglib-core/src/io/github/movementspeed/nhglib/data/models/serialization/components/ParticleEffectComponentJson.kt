package io.github.movementspeed.nhglib.data.models.serialization.components;

import com.badlogic.gdx.utils.JsonValue;
import io.github.movementspeed.nhglib.core.ecs.components.graphics.ParticleEffectComponent;
import io.github.movementspeed.nhglib.data.models.serialization.ComponentJson;

/**
 * Created by Fausto Napoli on 19/12/2016.
 */
public class ParticleEffectComponentJson extends ComponentJson {
    @Override
    public void parse(JsonValue jsonValue) {
        ParticleEffectComponent particleEffectComponent =
                nhg.entities.createComponent(entity, ParticleEffectComponent.class);

        particleEffectComponent.asset = jsonValue.getString("asset", "");
        output = particleEffectComponent;
    }
}
