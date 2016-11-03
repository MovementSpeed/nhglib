package io.github.voidzombie.nhglib.runtime.messaging;

import com.badlogic.gdx.utils.Array;
import io.github.voidzombie.nhglib.utils.data.Bundle;

/**
 * Created by Fausto Napoli on 01/11/2016.
 */
public class Messaging {
    private Array<EventListener> listeners;

    public Messaging() {
        listeners = new Array<EventListener>();
    }

    public void broadcastEvent(String name, Bundle data) {
        Event event = new Event(name, data);

        for (EventListener listener : listeners) {
            listener.onEvent(event);
        }
    }

    public void addListener(EventListener eventListener) {
        if (eventListener != null) {
            listeners.add(eventListener);
        }
    }
}
