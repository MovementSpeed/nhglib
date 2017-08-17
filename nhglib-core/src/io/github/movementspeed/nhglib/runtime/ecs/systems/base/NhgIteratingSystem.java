package io.github.movementspeed.nhglib.runtime.ecs.systems.base;

import com.artemis.Aspect;
import com.artemis.systems.IteratingSystem;

/**
 * Created by Fausto Napoli on 06/11/2016.
 */
public abstract class NhgIteratingSystem extends IteratingSystem {
    public NhgIteratingSystem(Aspect.Builder aspect) {
        super(aspect);
    }
}
