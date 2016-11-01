package io.github.voidzombie.nhglib.runtime.ecs.systems;

import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.runtime.ecs.components.BroadcastComponent;
import io.github.voidzombie.nhglib.runtime.messaging.BroadcastListener;
import io.github.voidzombie.nhglib.runtime.messaging.Event;

/**
 * Created by Fausto Napoli on 01/11/2016.
 */
public class BroadcastSystem extends IteratingSystem implements BroadcastListener {
    protected ComponentMapper<BroadcastComponent> broadcastMapper;

    private Array<Event> events;

    @SuppressWarnings("unchecked")
    public BroadcastSystem() {
        super(Aspect.all(BroadcastComponent.class));
        events = new Array<Event>();
    }

    @Override
    protected void process(int entityId) {
        BroadcastComponent broadcastComponent = broadcastMapper.get(entityId);

        for (Event e : events) {
            checkSubscription(e, broadcastComponent);
        }
    }

    @Override
    protected void end() {
        super.end();
        events.clear();
    }

    @Override
    public void onEvent(Event event) {
        events.add(event);
    }

    private void checkSubscription(Event event, BroadcastComponent subscriber) {
        if (subscriber != null && event.id.equals(subscriber.event.id)) {
            subscriber.event.data = event.data;
        }
    }
}
