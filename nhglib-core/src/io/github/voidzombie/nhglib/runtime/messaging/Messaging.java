package io.github.voidzombie.nhglib.runtime.messaging;

import com.badlogic.gdx.utils.Array;
import io.github.voidzombie.nhglib.utils.data.Bundle;

/**
 * Created by Fausto Napoli on 01/11/2016.
 */
public class Messaging {
    private Array<EventAdapter> eventAdapters;

    public Messaging() {
        eventAdapters = new Array<EventAdapter>();
    }

    public void broadcastEvent(String name) {
        broadcastEvent(name, null);
    }

    public void broadcastEvent(String name, Bundle data) {
        Event event = new Event(name, data);

        for (EventAdapter adapter : eventAdapters) {
            if (adapter.filter.equals(event.id) || adapter.filter.equals(0)) {
                adapter.eventListener.onEvent(event);
            }
        }
    }

    public void subscribe(String filter, EventListener eventListener) {
        if (filter != null && eventListener != null) {
            eventAdapters.add(new EventAdapter(filter, eventListener));
        }
    }

    public void subscribe(EventListener eventListener) {
        if (eventListener != null) {
            eventAdapters.add(new EventAdapter(eventListener));
        }
    }
}
