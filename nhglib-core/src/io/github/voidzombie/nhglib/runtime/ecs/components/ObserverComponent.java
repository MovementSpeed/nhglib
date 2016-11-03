package io.github.voidzombie.nhglib.runtime.ecs.components;

import com.artemis.PooledComponent;
import com.badlogic.gdx.utils.ArrayMap;
import io.github.voidzombie.nhglib.runtime.messaging.Event;

/**
 * Created by Fausto Napoli on 01/11/2016.
 * The subscribedEvents ArrayMap stores an Event as a key, and a Boolean to signal whether the Event has been triggered
 */
public class ObserverComponent extends PooledComponent {
    public ArrayMap<Event, Boolean> subscribedEvents = new ArrayMap<Event, Boolean>();

    @Override
    protected void reset() {
        subscribedEvents.clear();
    }

    public void subscribe(Event event) {
        subscribedEvents.put(event, false);
    }

    public void unsubscribe(Event event) {
        subscribedEvents.removeKey(event);
    }

    public void trigger(Event event) {
        ArrayMap.Keys<Event> eventKeys = subscribedEvents.keys();

        for (Event e : eventKeys) {
            if (e.equals(event)) {
                subscribedEvents.put(e, true);
            }
        }
    }

    public Boolean triggered(Event event) {
        Boolean triggered = null;
        int ind = subscribedEvents.indexOfKey(event);

        if (ind != -1) {
            triggered = subscribedEvents.getValueAt(ind);
            subscribedEvents.setValue(ind, false);
        }

        return triggered;
    }
}
