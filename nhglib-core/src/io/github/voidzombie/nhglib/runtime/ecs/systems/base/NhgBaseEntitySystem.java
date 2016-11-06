package io.github.voidzombie.nhglib.runtime.ecs.systems.base;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import io.github.voidzombie.nhglib.runtime.messaging.EventListener;

/**
 * Created by Fausto Napoli on 06/11/2016.
 */
public abstract class NhgBaseEntitySystem extends BaseEntitySystem implements EventListener {
    public NhgBaseEntitySystem(Aspect.Builder aspect) {
        super(aspect);
    }
}
