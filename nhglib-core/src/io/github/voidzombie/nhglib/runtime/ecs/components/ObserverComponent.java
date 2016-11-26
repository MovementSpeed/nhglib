package io.github.voidzombie.nhglib.runtime.ecs.components;

import com.artemis.PooledComponent;
import com.badlogic.gdx.utils.ArrayMap;
import io.github.voidzombie.nhglib.runtime.messaging.Message;

/**
 * Created by Fausto Napoli on 01/11/2016.
 * The listenedMessages ArrayMap stores a Message as a key, and a Boolean to signal whether the Message has been triggered
 */
public class ObserverComponent extends PooledComponent {
    public ArrayMap<Message, Boolean> listenedMessages = new ArrayMap<Message, Boolean>();

    @Override
    protected void reset() {
        listenedMessages.clear();
    }

    public void listen(Message message) {
        listenedMessages.put(message, false);
    }

    public void ignore(Message message) {
        listenedMessages.removeKey(message);
    }

    public void trigger(Message message) {
        ArrayMap.Keys<Message> messageKeys = listenedMessages.keys();

        for (Message e : messageKeys) {
            if (e.equals(message)) {
                listenedMessages.put(e, true);
            }
        }
    }

    public Boolean triggered(Message message) {
        Boolean triggered = null;
        int ind = listenedMessages.indexOfKey(message);

        if (ind != -1) {
            triggered = listenedMessages.getValueAt(ind);
            listenedMessages.setValue(ind, false);
        }

        return triggered;
    }
}
