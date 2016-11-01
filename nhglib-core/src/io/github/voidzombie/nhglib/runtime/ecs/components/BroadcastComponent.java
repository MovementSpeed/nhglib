package io.github.voidzombie.nhglib.runtime.ecs.components;

import com.artemis.Component;
import io.github.voidzombie.nhglib.runtime.messaging.Event;

/**
 * Created by Fausto Napoli on 01/11/2016.
 */
public class BroadcastComponent extends Component {
    public Event event;

    public void subscribeTo(String eventName) {
        event = new Event(eventName, null);
    }
}
