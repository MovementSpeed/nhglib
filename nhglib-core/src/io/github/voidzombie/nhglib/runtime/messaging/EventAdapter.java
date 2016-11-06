package io.github.voidzombie.nhglib.runtime.messaging;

import io.github.voidzombie.nhglib.utils.data.StringUtils;

/**
 * Created by Fausto Napoli on 06/11/2016.
 */
public class EventAdapter {
    public Integer filter;
    public EventListener eventListener;

    public EventAdapter(EventListener eventListener) {
        this("", eventListener);
    }

    public EventAdapter(String filter, EventListener eventListener) {
        this.filter = StringUtils.idFromString(filter);
        this.eventListener = eventListener;
    }
}
