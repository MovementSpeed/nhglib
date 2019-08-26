package io.github.movementspeed.nhglib.core.ecs.components.common

import com.artemis.PooledComponent
import com.badlogic.gdx.utils.Array
import io.github.movementspeed.nhglib.core.messaging.Message

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
class MessageComponent : PooledComponent() {
    val messages: Array<Message>
    private val filters: Array<String>

    init {
        messages = Array()
        filters = Array()
    }

    override fun reset() {
        messages.clear()
        filters.clear()
    }

    fun subscribe(vararg filters: String) {
        /*Nhg.messaging.get(filters)
                .subscribe(new Consumer<Message>() {
                    @Override
                    public void accept(Message message) throws Exception {
                        messages.add(message);
                    }
                });*/

        this.filters.clear()
        this.filters.addAll(*filters)
    }

    fun consume(message: Message) {
        messages.removeValue(message, false)
    }
}
