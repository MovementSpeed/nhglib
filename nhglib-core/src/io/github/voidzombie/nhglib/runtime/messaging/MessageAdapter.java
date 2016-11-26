package io.github.voidzombie.nhglib.runtime.messaging;

import io.github.voidzombie.nhglib.utils.data.StringUtils;

/**
 * Created by Fausto Napoli on 06/11/2016.
 */
public class MessageAdapter {
    public Integer filter;
    public MessageListener messageListener;

    public MessageAdapter(MessageListener messageListener) {
        this("", messageListener);
    }

    public MessageAdapter(String filter, MessageListener messageListener) {
        this.filter = StringUtils.idFromString(filter);
        this.messageListener = messageListener;
    }
}
