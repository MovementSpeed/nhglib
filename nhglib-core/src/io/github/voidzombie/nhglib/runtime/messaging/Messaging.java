package io.github.voidzombie.nhglib.runtime.messaging;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class Messaging {
    private final PublishSubject<Message> messagePublisher;

    public Messaging() {
        messagePublisher = PublishSubject.create();
    }

    public void send(Message message) {
        messagePublisher.onNext(message);
    }

    public Observable<Message> get(String ... filters) {
        return messagePublisher.filter(message -> {
            boolean res = false;

            for (String filter : filters) {
                if (message.is(filter)) {
                    res = true;
                    break;
                }
            }

            return res;
        });
    }
}
