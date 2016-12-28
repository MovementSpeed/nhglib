package io.github.voidzombie.tests.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.utils.Array;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.runtime.ecs.components.common.MessageComponent;
import io.github.voidzombie.nhglib.runtime.ecs.systems.base.ThreadedIteratingSystem;
import io.github.voidzombie.nhglib.runtime.messaging.Message;

/**
 * Created by Fausto Napoli on 01/11/2016.
 */
public class TestSystem extends ThreadedIteratingSystem {
    private ComponentMapper<MessageComponent> messageMapper;

    @SuppressWarnings("unchecked")
    public TestSystem() {
        super(Aspect.all(MessageComponent.class));
    }

    @Override
    protected void begin() {
        super.begin();
    }

    @Override
    protected void process(int entityId) {
        MessageComponent messageComponent = messageMapper.get(entityId);
        Array<Message> messages = new Array<>(messageComponent.getMessages());

        for (Message message : messages) {
            if (message.is("fire")) {
                NHG.logger.log(this, "Message \"fire\" received by entity %d", entityId);
                messageComponent.consume(message);
            } else if (message.is("fly")) {
                NHG.logger.log(this, "Message \"fly\" received by entity %d", entityId);
                messageComponent.consume(message);
            }
        }
    }

    @Override
    protected void end() {
        super.end();
    }
}
