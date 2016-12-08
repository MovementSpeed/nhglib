package io.github.voidzombie.tests.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.utils.PerformanceCounters;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.runtime.ecs.components.MessageComponent;
import io.github.voidzombie.nhglib.runtime.ecs.systems.base.ThreadedIteratingSystem;
import io.github.voidzombie.nhglib.runtime.messaging.Message;
import io.github.voidzombie.nhglib.utils.debug.Debug;
import io.github.voidzombie.tests.Main;

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
        messageComponent.getMessages().subscribe((message -> {
            if (message.is("fire")) {
                NHG.logger.log(this, "Message \"fire\" received by entity %d", entityId);
            } else if (message.is("fly")) {
                NHG.logger.log(this, "Message \"fly\" received by entity %d", entityId);
            }
        }));
    }

    @Override
    protected void end() {
        super.end();
    }
}
