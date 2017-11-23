package io.github.movementspeed.nhglib.core.ecs.systems.impl;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import io.github.movementspeed.nhglib.core.ecs.components.graphics.ParticleEffectComponent;
import io.github.movementspeed.nhglib.core.ecs.components.scenes.NodeComponent;
import io.github.movementspeed.nhglib.core.ecs.systems.base.BaseRenderingSystem;
import io.github.movementspeed.nhglib.core.ecs.utils.Entities;
import io.github.movementspeed.nhglib.graphics.particles.ParticleEffectProvider;
import io.github.movementspeed.nhglib.graphics.particles.PointSpriteSoftParticleBatch;
import io.github.movementspeed.nhglib.graphics.shaders.particles.ParticleShader;

public class ParticleRenderingSystem extends BaseRenderingSystem {
    private boolean initialized;

    private ParticleSystem particleSystem;
    private PointSpriteSoftParticleBatch pointSpriteBatch;
    private ParticleEffectProvider particleEffectProvider;

    private ComponentMapper<NodeComponent> nodeMapper;
    private ComponentMapper<ParticleEffectComponent> particleEffectMapper;

    public ParticleRenderingSystem(Entities entities) {
        super(Aspect.all(NodeComponent.class, ParticleEffectComponent.class), entities);
        particleSystem = new ParticleSystem();
        particleEffectProvider = new ParticleEffectProvider();
    }

    @Override
    protected void begin() {
        super.begin();

        if (!initialized) {
            initialized = true;

            ParticleShader.Config config = new ParticleShader.Config(
                    Gdx.files.internal("shaders/particle_shader.vert").readString(),
                    Gdx.files.internal("shaders/particle_shader.frag").readString());

            config.type = ParticleShader.ParticleType.Point;
            config.align = ParticleShader.AlignMode.Screen;

            pointSpriteBatch = new PointSpriteSoftParticleBatch(renderingSystem, 1000, config);
            particleSystem.add(pointSpriteBatch);
        }
    }

    @Override
    protected void process(int entityId) {
        NodeComponent nodeComponent = nodeMapper.get(entityId);
        ParticleEffectComponent particleEffectComponent = particleEffectMapper.get(entityId);
        renderableProviders.add(particleSystem);

        if (particleEffectComponent.state == ParticleEffectComponent.State.READY) {
            ParticleEffect particleEffect = particleEffectComponent.particleEffect;

            if (!particleEffectComponent.added) {
                particleSystem.add(particleEffect);
                particleEffectComponent.added = true;
            }

            particleEffect.setTransform(nodeComponent.getTransform());
        }
    }

    @Override
    protected void end() {
        super.end();

        for (int i = 0; i < cameras.size; i++) {
            Camera camera = cameras.get(i);

            pointSpriteBatch.setCamera(camera);
            particleSystem.update();
            particleSystem.begin();
            particleSystem.draw();
            particleSystem.end();
        }
    }

    public ParticleSystem getParticleSystem() {
        return particleSystem;
    }

    public ParticleEffectProvider getParticleEffectProvider() {
        return particleEffectProvider;
    }
}
