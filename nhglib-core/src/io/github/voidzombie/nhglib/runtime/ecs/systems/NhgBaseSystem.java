package io.github.voidzombie.nhglib.runtime.ecs.systems;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import io.github.voidzombie.nhglib.runtime.messaging.EventListener;

/**
 * Created by Fausto Napoli on 06/11/2016.
 */
public abstract class NhgBaseSystem extends BaseEntitySystem implements EventListener {
    public NhgBaseSystem(Aspect.Builder aspect) {
        super(aspect);
    }
}
