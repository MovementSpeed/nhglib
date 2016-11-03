package io.github.voidzombie.tests.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.runtime.ecs.components.ObserverComponent;
import io.github.voidzombie.nhglib.runtime.ecs.systems.ThreadedIteratingSystem;
import io.github.voidzombie.nhglib.runtime.messaging.Event;

/**
 * Created by Fausto Napoli on 01/11/2016.
 */
public class TestSystem extends ThreadedIteratingSystem {
    private ComponentMapper<ObserverComponent> observerMapper;

    private Event fireEvent;
    private Event flyEvent;

    @SuppressWarnings("unchecked")
    public TestSystem() {
        super(Aspect.all(ObserverComponent.class));

        fireEvent = new Event("fire");
        flyEvent = new Event("fly");
    }

    @Override
    protected void process(int entityId) {
        ObserverComponent observerComponent = observerMapper.get(entityId);

        Boolean fireTriggered = observerComponent.triggered(fireEvent);
        Boolean flyTriggered = observerComponent.triggered(flyEvent);

        if (fireTriggered) {
            NHG.logger.log(this, "fire!");
        }

        if (flyTriggered) {
            NHG.logger.log(this, "fly!");
        }
    }

    @Override
    protected void end() {
        super.end();
    }
}
