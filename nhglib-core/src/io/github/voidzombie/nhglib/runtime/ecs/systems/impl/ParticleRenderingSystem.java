package io.github.voidzombie.nhglib.runtime.ecs.systems.impl;

import com.artemis.Aspect;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.graphics.g3d.particles.batches.PointSpriteParticleBatch;
import io.github.voidzombie.nhglib.runtime.ecs.components.graphics.ParticleEffectComponent;
import io.github.voidzombie.nhglib.runtime.ecs.components.scenes.NodeComponent;
import io.github.voidzombie.nhglib.runtime.ecs.systems.base.BaseRenderingSystem;
import io.github.voidzombie.nhglib.runtime.ecs.utils.Entities;

public class ParticleRenderingSystem extends BaseRenderingSystem {
    public AssetManager assets;
    //private ParticleEffect currentEffects;
    private ParticleSystem particleSystem;
    private PointSpriteParticleBatch pointSpriteBatch;

    public ParticleRenderingSystem(Entities entities) {
        super(Aspect.all(NodeComponent.class, ParticleEffectComponent.class), entities);

        assets = new AssetManager();
        particleSystem = new ParticleSystem();

        pointSpriteBatch = new PointSpriteParticleBatch();

        particleSystem = new ParticleSystem();
        particleSystem.add(pointSpriteBatch);

        /*ParticleEffectLoader.ParticleEffectLoadParameter loadParam = new ParticleEffectLoader.ParticleEffectLoadParameter(particleSystem.getBatches());
        ParticleEffectLoader loader = new ParticleEffectLoader(new InternalFileHandleResolver());

        assets.setLoader(ParticleEffect.class, loader);
        assets.load("particles/kart_start_smoke.nhfx", ParticleEffect.class, loadParam);*/

        // halt the main thread until assets are loaded.
        // this is bad for actual games, but okay for demonstration purposes.
        //assets.finishLoading();

        /*currentEffects = assets.get("particles/kart_start_smoke.nhfx", ParticleEffect.class).copy();
        currentEffects.init();
        currentEffects.scale(new Vector3(0.3f, 0.3f, 0.3f));*/

        //particleSystem.add(currentEffects);
    }

    @Override
    protected void process(int entityId) {
    }

    @Override
    protected void end() {
        super.end();
        renderableProviders.clear();

        //currentEffects.rotate(Vector3.Y, 12);

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
}
