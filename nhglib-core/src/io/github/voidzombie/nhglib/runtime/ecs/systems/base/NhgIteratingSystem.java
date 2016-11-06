package io.github.voidzombie.nhglib.runtime.ecs.systems.base;

import com.artemis.Aspect;
import com.artemis.systems.IteratingSystem;
import io.github.voidzombie.nhglib.runtime.messaging.EventListener;

/**
 * Created by Fausto Napoli on 06/11/2016.
 */
public abstract class NhgIteratingSystem extends IteratingSystem implements EventListener {
    public NhgIteratingSystem(Aspect.Builder aspect) {
        super(aspect);
    }
}
