package io.github.voidzombie.nhglib.runtime.ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;
import io.github.voidzombie.nhglib.runtime.ecs.components.ObserverComponent;
import io.github.voidzombie.nhglib.runtime.messaging.EventListener;
import io.github.voidzombie.nhglib.runtime.messaging.Event;

/**
 * Created by Fausto Napoli on 01/11/2016.
 */
public class EventSystem extends IteratingSystem implements EventListener {
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

        for (Event event : events) {
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
