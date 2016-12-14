package io.github.voidzombie.nhglib.runtime.ecs.components.common;

import com.artemis.PooledComponent;
import com.badlogic.gdx.utils.Array;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.runtime.messaging.Message;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class MessageComponent extends PooledComponent {
    private Array<Message> messages;
    private Array<String> filters;

    public MessageComponent() {
        messages = new Array<>();
        filters = new Array<>();
    }

    @Override
    protected void reset() {
        messages.clear();
        filters.clear();
    }

    public void subscribe(String ... filters) {
        NHG.messaging.get(filters)
                .subscribe(message -> messages.add(message));

        this.filters.clear();
        this.filters.addAll(filters);
    }

    public void consume(Message message) {
        messages.removeValue(message, false);
    }

    public Array<Message> getMessages() {
        return messages;
    }

    private Boolean filter(Message message) {
        Boolean res = false;

        for (int i = 0; i < filters.size; i++) {
            String filter = filters.get(i);

            if (message.is(filter)) {
                res = true;
                break;
            }
        }

        return res;
    }
}
