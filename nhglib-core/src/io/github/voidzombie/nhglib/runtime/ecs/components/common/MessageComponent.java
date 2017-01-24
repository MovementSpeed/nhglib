package io.github.voidzombie.nhglib.runtime.ecs.components.common;

import com.artemis.PooledComponent;
import com.badlogic.gdx.utils.Array;
import io.github.voidzombie.nhglib.Nhg;
import io.github.voidzombie.nhglib.runtime.messaging.Message;
import io.reactivex.functions.Consumer;

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
        Nhg.messaging.get(filters)
                .subscribe(new Consumer<Message>() {
                    @Override
                    public void accept(Message message) throws Exception {
                        messages.add(message);
                    }
                });

        this.filters.clear();
        this.filters.addAll(filters);
    }

    public void consume(Message message) {
        messages.removeValue(message, false);
    }

    public Array<Message> getMessages() {
        return messages;
    }
}
