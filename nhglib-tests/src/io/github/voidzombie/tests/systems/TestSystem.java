package io.github.voidzombie.tests.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.runtime.ecs.components.ObserverComponent;
import io.github.voidzombie.nhglib.runtime.ecs.systems.ThreadedIteratingSystem;
import io.github.voidzombie.nhglib.runtime.messaging.Event;
import io.github.voidzombie.tests.Main;

/**
 * Created by Fausto Napoli on 01/11/2016.
 */
public class TestSystem extends ThreadedIteratingSystem {
    private ComponentMapper<ObserverComponent> observerMapper;

    private Event fireEvent;

    @SuppressWarnings("unchecked")
    public TestSystem() {
        super(Aspect.all(ObserverComponent.class));

        fireEvent = new Event("fire");
    }

    @Override
    protected void begin() {
        super.begin();
        Main.timeStart = System.currentTimeMillis();
    }

    @Override
    protected void process(int entityId) {
        ObserverComponent observerComponent = observerMapper.get(entityId);
        Boolean fireTriggered = observerComponent.triggered(fireEvent);

        if (fireTriggered) {
            NHG.logger.log(this, "fire! %d", System.currentTimeMillis());
        }
    }

    @Override
    protected void end() {
        super.end();

        Main.timeEnd = System.currentTimeMillis();
        Main.average += (Main.timeEnd - Main.timeStart);
        Main.average /= 2;

        //NHG.logger.log(this, "time %d average %d", Main.timeEnd - Main.timeStart, Main.average);
    }

    @Override
    public void onEvent(Event event) {

    }
}
