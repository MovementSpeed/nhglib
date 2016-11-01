package io.github.voidzombie.nhglib.runtime.messaging;

import com.badlogic.gdx.utils.Array;
import io.github.voidzombie.nhglib.utils.data.Bundle;

/**
 * Created by Fausto Napoli on 01/11/2016.
 */
public class Broadcaster {
    private Array<BroadcastListener> listeners;

    public Broadcaster() {
        listeners = new Array<BroadcastListener>();
    }

    public void broadcastEvent(String name, Bundle data) {
        Event event = new Event(name, data);

        for (BroadcastListener listener : listeners) {
            listener.onEvent(event);
        }
    }

    public void addListener(BroadcastListener broadcastListener) {
        if (broadcastListener != null) {
            listeners.add(broadcastListener);
        }
    }
}
