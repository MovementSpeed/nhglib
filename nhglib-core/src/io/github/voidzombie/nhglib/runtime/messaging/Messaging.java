package io.github.voidzombie.nhglib.runtime.messaging;

import com.badlogic.gdx.utils.Array;
import io.github.voidzombie.nhglib.utils.data.Bundle;

/**
 * Created by Fausto Napoli on 01/11/2016.
 */
public class Messaging {
    private Array<MessageAdapter> eventAdapters;

    public Messaging() {
        eventAdapters = new Array<MessageAdapter>();
    }

    public void sendMessage(String name) {
        sendMessage(name, null);
    }

    public void sendMessage(String name, Bundle data) {
        Message message = new Message(name, data);

        for (MessageAdapter adapter : eventAdapters) {
            if (adapter.filter.equals(message.id) || adapter.filter.equals(0)) {
                adapter.messageListener.onMessage(message);
            }
        }
    }

    public void addListener(String filter, MessageListener messageListener) {
        if (filter != null && messageListener != null) {
            eventAdapters.add(new MessageAdapter(filter, messageListener));
        }
    }

    public void addListener(MessageListener messageListener, String ... filters) {
        if (messageListener != null) {
            for (String filter : filters) {
                if (filter != null) {
                    eventAdapters.add(new MessageAdapter(filter, messageListener));
                }
            }
        }
    }

    public void addListener(MessageListener messageListener) {
        if (messageListener != null) {
            eventAdapters.add(new MessageAdapter(messageListener));
        }
    }
}
