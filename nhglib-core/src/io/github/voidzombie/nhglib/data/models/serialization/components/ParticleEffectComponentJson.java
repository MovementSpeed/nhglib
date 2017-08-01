package io.github.voidzombie.nhglib.data.models.serialization.components;

import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.data.models.serialization.ComponentJson;
import io.github.voidzombie.nhglib.runtime.ecs.components.graphics.ParticleEffectComponent;

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
