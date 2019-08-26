package io.github.movementspeed.nhglib.core.messaging

import io.reactivex.Observable
import io.reactivex.functions.Predicate
import io.reactivex.subjects.PublishSubject

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
class Messaging {
    private val messagePublisher: PublishSubject<Message>

    init {
        messagePublisher = PublishSubject.create()
    }

    fun send(message: Message) {
        messagePublisher.onNext(message)
    }

    operator fun get(vararg filters: String): Observable<Message> {
        return messagePublisher.filter { message ->
            var res = false

            for (filter in filters) {
                if (message.`is`(filter)) {
                    res = true
                    break
                }
            }

            res
        }
    }
}
