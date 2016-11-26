package io.github.voidzombie.nhglib.runtime.ecs.systems.impl;

import com.artemis.Aspect;
import io.github.voidzombie.nhglib.runtime.ecs.systems.base.ThreadedIteratingSystem;
import io.github.voidzombie.nhglib.runtime.messaging.Message;

/**
 * Created by Fausto Napoli on 01/11/2016.
 * ..
 */
public class AssetSystem extends ThreadedIteratingSystem {

    @SuppressWarnings("unchecked")
    public AssetSystem() {
        super(Aspect.all());
    }

    @Override
    protected void process(int entityId) {
    }

    @Override
    protected void end() {
        super.end();
    }

    @Override
    public void onMessage(Message message) {
    }
}
