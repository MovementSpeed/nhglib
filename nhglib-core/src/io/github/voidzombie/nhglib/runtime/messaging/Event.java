package io.github.voidzombie.nhglib.runtime.messaging;

import io.github.voidzombie.nhglib.utils.data.Bundle;
import io.github.voidzombie.nhglib.utils.data.StringUtils;

/**
 * Created by Fausto Napoli on 01/11/2016.
 */
public class Event {
    public Integer id;
    public Bundle data;

    public Event(String name) {
        this(name, null);
    }

    public Event(String name, Bundle data) {
        id = StringUtils.idFromString(name);

        if (data == null) {
            this.data = new Bundle();
            this.data.put("eventId", id);
        } else {
            this.data = data;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;
        return id.equals(event.id);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }
}
