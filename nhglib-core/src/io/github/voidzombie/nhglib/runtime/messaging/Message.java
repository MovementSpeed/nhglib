package io.github.voidzombie.nhglib.runtime.messaging;

import io.github.voidzombie.nhglib.utils.data.Bundle;
import io.github.voidzombie.nhglib.utils.data.StringUtils;

/**
 * Created by Fausto Napoli on 01/11/2016.
 * Data structure for a message.
 */
public class Message {
    public Integer id;
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
        return id.equals(message.id);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }

    public boolean is(String name) {
        Integer id = StringUtils.idFromString(name);
        return this.id.equals(id);
    }
}
