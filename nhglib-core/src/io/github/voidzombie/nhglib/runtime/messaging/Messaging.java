package io.github.voidzombie.nhglib.runtime.messaging;

import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class Messaging {
    private final PublishSubject<Message> messageBus;

    public Messaging() {
        messageBus = PublishSubject.create();
    }

    public void send(Message message) {
        messageBus.onNext(message);
    }

    public void subscribe(Consumer<Message> messageConsumer) {
        messageBus.subscribe(messageConsumer);
    }
}
