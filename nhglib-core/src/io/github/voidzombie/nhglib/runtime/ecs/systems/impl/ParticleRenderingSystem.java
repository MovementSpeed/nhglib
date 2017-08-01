package io.github.voidzombie.nhglib.runtime.ecs.systems.impl;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.graphics.g3d.particles.batches.PointSpriteParticleBatch;
import io.github.voidzombie.nhglib.graphics.particles.ParticleEffectProvider;
import io.github.voidzombie.nhglib.runtime.ecs.components.graphics.ParticleEffectComponent;
import io.github.voidzombie.nhglib.runtime.ecs.components.scenes.NodeComponent;
import io.github.voidzombie.nhglib.runtime.ecs.systems.base.BaseRenderingSystem;
import io.github.voidzombie.nhglib.runtime.ecs.utils.Entities;

public class ParticleRenderingSystem extends BaseRenderingSystem {
    //private ParticleEffect currentEffects;
    private ParticleSystem particleSystem;
    private PointSpriteParticleBatch pointSpriteBatch;
    private ParticleEffectProvider particleEffectProvider;

    private ComponentMapper<NodeComponent> nodeMapper;
    private ComponentMapper<ParticleEffectComponent> particleEffectMapper;

    public ParticleRenderingSystem(Entities entities) {
        super(Aspect.all(NodeComponent.class, ParticleEffectComponent.class), entities);

        pointSpriteBatch = new PointSpriteParticleBatch();
        particleEffectProvider = new ParticleEffectProvider();

        particleSystem = new ParticleSystem();
        particleSystem.add(pointSpriteBatch);
    }

    @Override
    protected void process(int entityId) {
        NodeComponent nodeComponent = nodeMapper.get(entityId);
        ParticleEffectComponent particleEffectComponent = particleEffectMapper.get(entityId);

        if (particleEffectComponent.state == ParticleEffectComponent.State.READY) {
            ParticleEffect particleEffect = particleEffectComponent.particleEffect;

            if (!particleEffectComponent.added) {
                particleSystem.add(particleEffect);
                particleEffectComponent.added = true;
            }

            nodeComponent.rotate(0, -25, 0, true);
            particleEffect.setTransform(nodeComponent.getTransform());
        }
    }

    @Override
    protected void end() {
        super.end();
        renderableProviders.clear();

        for (int i = 0; i < cameras.size; i++) {
            Camera camera = cameras.get(i);

            pointSpriteBatch.setCamera(camera);
            particleSystem.update();
            particleSystem.begin();
            particleSystem.draw();
            particleSystem.end();

            addRenderableProviders(particleSystem);
        }
    }

    public ParticleSystem getParticleSystem() {
        return particleSystem;
    }

    public ParticleEffectProvider getParticleEffectProvider() {
        return particleEffectProvider;
    }
}
