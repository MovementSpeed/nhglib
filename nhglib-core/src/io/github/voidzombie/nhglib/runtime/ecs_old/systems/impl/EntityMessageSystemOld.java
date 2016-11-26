package io.github.voidzombie.nhglib.runtime.ecs_old.systems.impl;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.utils.Array;
import io.github.voidzombie.nhglib.runtime.ecs.systems.base.ThreadedIteratingSystem;
import io.github.voidzombie.nhglib.runtime.ecs_old.components.ObserverComponent;
import io.github.voidzombie.nhglib.runtime.messaging.Message;

/**
 * Created by Fausto Napoli on 01/11/2016.
 * Transfers messages to entities, not sure about this anyway.
 */
public class EntityMessageSystemOld extends ThreadedIteratingSystem {
    private ComponentMapper<ObserverComponent> observerMapper;
    private Array<Message> messages;

    @SuppressWarnings("unchecked")
    public EntityMessageSystemOld() {
        super(Aspect.all(ObserverComponent.class));
        messages = new Array<Message>();
    }

    @Override
    protected void process(int entityId) {
        ObserverComponent observerComponent = observerMapper.get(entityId);

        for (int i = 0; i < messages.size; i++) {
            Message message = messages.get(i);

            for (Message subMessage : observerComponent.listenedMessages.keys()) {
                if (message.equals(subMessage)) {
                    subMessage.data = message.data;
                    observerComponent.listenedMessages.put(subMessage, true);
                }
            }
        }
    }

    @Override
    protected void end() {
        super.end();
        messages.clear();
    }

    @Override
    public void onMessage(Message message) {
        messages.add(message);
    }
}
