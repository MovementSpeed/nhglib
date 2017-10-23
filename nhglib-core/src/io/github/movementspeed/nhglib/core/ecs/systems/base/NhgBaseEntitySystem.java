package io.github.movementspeed.nhglib.core.ecs.systems.base;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;

/**
 * Created by Fausto Napoli on 06/11/2016.
 */
public abstract class NhgBaseEntitySystem extends BaseEntitySystem {
    public NhgBaseEntitySystem(Aspect.Builder aspect) {
        super(aspect);
    }
}
