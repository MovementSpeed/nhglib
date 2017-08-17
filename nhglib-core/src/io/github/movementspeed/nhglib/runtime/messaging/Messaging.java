package io.github.movementspeed.nhglib.runtime.messaging;

import io.reactivex.Observable;
import io.reactivex.functions.Predicate;
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

    public Observable<Message> get(final String... filters) {
        return messagePublisher.filter(new Predicate<Message>() {
            @Override
            public boolean test(Message message) throws Exception {
                boolean res = false;

                for (String filter : filters) {
                    if (message.is(filter)) {
                        res = true;
                        break;
                    }
                }

                return res;
            }
        });
    }
}
