package io.github.voidzombie.nhglib.runtime.ecs.components;

import com.artemis.Component;
import com.badlogic.gdx.utils.Array;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.runtime.messaging.Message;
import io.github.voidzombie.nhglib.runtime.messaging.MessageListener;

/**
 * Created by Fausto Napoli on 26/11/2016.
 * Contains messages and data received from the engine/game.
 */
public class MessageComponent extends Component implements MessageListener {
    private Array<Message> messages;

    public MessageComponent() {
        this.messages = new Array<Message>();
    }

    public void listen(String ... filters) {
        NHG.messaging.addListener(this, filters);
    }

    @Override
    public void onMessage(Message message) {
        addMessage(message);
    }

    public boolean hasNext() {
        return messages.size > 0;
    }

    /**
     * @return the next message, null if there are none.
     */
    public Message nextMessage() {
        Message message = null;

        if (messages.size > 0) {
            message = messages.pop();
        }

        return message;
    }

    private void addMessage(Message message) {
        if (message != null && message.id != null) {
            messages.add(message);
        }
    }
}
