package io.github.voidzombie.nhglib.runtime.messaging;

import com.badlogic.gdx.utils.Array;
import io.github.voidzombie.nhglib.utils.data.Bundle;

/**
 * Created by Fausto Napoli on 01/11/2016.
 */
public class Messaging {
    private Array<MessageAdapter> messageAdapters;

    public Messaging() {
        messageAdapters = new Array<MessageAdapter>();
    }

    public void sendMessage(String name) {
        sendMessage(name, null);
    }

    public void sendMessage(String name, Bundle data) {
        Message message = new Message(name, data);

        for (MessageAdapter adapter : messageAdapters) {
            if (adapter.filter.equals(message.id) || adapter.filter.equals(0)) {
                adapter.messageListener.onMessage(message);
            }
        }
    }

    public void addListener(String filter, MessageListener messageListener) {
        if (filter != null && messageListener != null) {
            messageAdapters.add(new MessageAdapter(filter, messageListener));
        }
    }

    public void addListener(MessageListener messageListener, String ... filters) {
        if (messageListener != null) {
            for (String filter : filters) {
                if (filter != null) {
                    messageAdapters.add(new MessageAdapter(filter, messageListener));
                }
            }
        }
    }

    public void addListener(MessageListener messageListener) {
        if (messageListener != null) {
            messageAdapters.add(new MessageAdapter(messageListener));
        }
    }

    public void removeListener(MessageListener messageListener) {
        for (int i = 0; i < messageAdapters.size; i++) {
            MessageAdapter adapter = messageAdapters.get(i);

            if (adapter.messageListener == messageListener) {
                messageAdapters.removeValue(adapter, true);
            }
        }
    }
}
