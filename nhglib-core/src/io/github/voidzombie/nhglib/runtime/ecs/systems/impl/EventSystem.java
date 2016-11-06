package io.github.voidzombie.nhglib.runtime.ecs.systems.impl;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.utils.Array;
import io.github.voidzombie.nhglib.runtime.ecs.components.ObserverComponent;
import io.github.voidzombie.nhglib.runtime.ecs.systems.base.ThreadedIteratingSystem;
import io.github.voidzombie.nhglib.runtime.messaging.Event;

/**
 * Created by Fausto Napoli on 01/11/2016.
 */
public class EventSystem extends ThreadedIteratingSystem {
    private ComponentMapper<ObserverComponent> observerMapper;
    private Array<Event> events;

    @SuppressWarnings("unchecked")
    public EventSystem() {
        super(Aspect.all(ObserverComponent.class));
        events = new Array<Event>();
    }

    @Override
    protected void process(int entityId) {
        ObserverComponent observerComponent = observerMapper.get(entityId);

        for (int i = 0; i < events.size; i++) {
            Event event = events.get(i);

            for (Event subEvent : observerComponent.subscribedEvents.keys()) {
                if (event.equals(subEvent)) {
                    subEvent.data = event.data;
                    observerComponent.subscribedEvents.put(subEvent, true);
                }
            }
        }
    }

    @Override
    protected void end() {
        super.end();
        events.clear();
    }

    @Override
    public void onEvent(Event event) {
        events.add(event);
    }
}
