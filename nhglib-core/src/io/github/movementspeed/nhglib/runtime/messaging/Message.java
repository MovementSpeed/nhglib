package io.github.movementspeed.nhglib.runtime.messaging;

import io.github.movementspeed.nhglib.utils.data.Bundle;
import io.github.movementspeed.nhglib.utils.data.StringUtils;

/**
 * Created by Fausto Napoli on 01/11/2016.
 * Data structure for a message.
 */
public class Message {
    public int id;
    public Bundle data;

    public Message(String name) {
        this(name, null);
    }

    public Message(String name, Bundle data) {
        id = StringUtils.idFromString(name);
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;
        return id == message.id;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }

    public boolean is(String name) {
        int id = StringUtils.idFromString(name);
        return this.id == id;
    }
}
