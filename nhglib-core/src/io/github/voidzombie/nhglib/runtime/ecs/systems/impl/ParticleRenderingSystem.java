package io.github.voidzombie.nhglib.runtime.ecs.systems.impl;

import com.artemis.Aspect;
import io.github.voidzombie.nhglib.runtime.ecs.systems.base.BaseRenderingSystem;
import io.github.voidzombie.nhglib.runtime.ecs.utils.Entities;

public class ParticleRenderingSystem extends BaseRenderingSystem {

    public ParticleRenderingSystem(Aspect.Builder aspect, Entities entities) {
        super(aspect, entities);
    }

    @Override
    protected void process(int entityId) {

    }
}
