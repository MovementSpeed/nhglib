package io.github.voidzombie.tests.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.runtime.ecs.components.ObserverComponent;
import io.github.voidzombie.nhglib.runtime.ecs.systems.base.ThreadedIteratingSystem;
import io.github.voidzombie.nhglib.runtime.messaging.Message;
import io.github.voidzombie.tests.Main;

/**
 * Created by Fausto Napoli on 01/11/2016.
 */
public class TestSystem extends ThreadedIteratingSystem {
    private ComponentMapper<ObserverComponent> observerMapper;

    private Message fireMessage;

    @SuppressWarnings("unchecked")
    public TestSystem() {
        super(Aspect.all(ObserverComponent.class));

        fireMessage = new Message("fire");
    }

    @Override
    protected void begin() {
        super.begin();
        Main.timeStart = System.currentTimeMillis();
    }

    @Override
    protected void process(int entityId) {
        ObserverComponent observerComponent = observerMapper.get(entityId);
        Boolean fireTriggered = observerComponent.triggered(fireMessage);

        if (fireTriggered) {
            NHG.logger.log(this, "Message received by entity %d", entityId);
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
    public void onMessage(Message message) {

    }
}
