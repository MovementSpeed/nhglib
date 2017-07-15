package io.github.voidzombie.tests.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.utils.Array;
import io.github.voidzombie.nhglib.runtime.ecs.components.common.MessageComponent;
import io.github.voidzombie.nhglib.runtime.ecs.components.scenes.NodeComponent;
import io.github.voidzombie.nhglib.runtime.ecs.systems.base.NhgIteratingSystem;
import io.github.voidzombie.nhglib.runtime.messaging.Message;
import io.github.voidzombie.nhglib.utils.debug.NhgLogger;

/**
 * Created by Fausto Napoli on 13/12/2016.
 */
public class TestNodeSystem extends NhgIteratingSystem {
    private ComponentMapper<NodeComponent> nodeMapper;
    private ComponentMapper<MessageComponent> messageMapper;

    public TestNodeSystem() {
        super(Aspect.all(NodeComponent.class, MessageComponent.class));
    }

    @Override
    protected void process(int entityId) {
        NodeComponent nodeComponent = nodeMapper.get(entityId);

        MessageComponent messageComponent = messageMapper.get(entityId);
        Array<Message> messages = messageComponent.getMessages();

        for (Message message : messages) {
            if (message.is("printNode")) {
                NhgLogger.log(this, "id: %d, x: %f, y: %f, z: %f",
                        nodeComponent.id,
                        nodeComponent.getX(),
                        nodeComponent.getY(),
                        nodeComponent.getZ());

                messageComponent.consume(message);
            }
        }
    }

    @Override
    protected void end() {
        super.end();
    }
}
