package io.github.voidzombie.nhglib.runtime.ecs.components.graphics;

import com.artemis.PooledComponent;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import io.github.voidzombie.nhglib.enums.LightType;

/**
 * Created by Fausto Napoli on 14/03/2017.
 */
public class LightComponent extends PooledComponent {
    public BaseLight light;
    public LightType type;

    @Override
    protected void reset() {
        light = null;
        type = null;
    }
}
