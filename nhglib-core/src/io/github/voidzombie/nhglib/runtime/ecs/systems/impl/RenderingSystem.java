package io.github.voidzombie.nhglib.runtime.ecs.systems.impl;

import com.artemis.BaseSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.utils.Array;
import io.github.voidzombie.nhglib.Nhg;
import io.github.voidzombie.nhglib.graphics.shaders.tiledForward.TiledForwardShaderProvider;
import io.github.voidzombie.nhglib.runtime.ecs.interfaces.RenderingSystemInterface;
import io.github.voidzombie.nhglib.runtime.ecs.systems.base.BaseRenderingSystem;
import io.github.voidzombie.nhglib.runtime.ecs.utils.Entities;
import io.github.voidzombie.nhglib.utils.graphics.GLUtils;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class RenderingSystem extends BaseSystem {
    private ShaderProvider shaderProvider;
    private PhysicsSystem physicsSystem;
    private CameraSystem cameraSystem;
    private Environment environment;
    private DebugDrawer debugDrawer;
    private Entities entities;
    private Color clearColor;

    private Array<Camera> cameras;
    private Array<ModelBatch> modelBatches;
    private Array<RenderingSystemInterface> renderingInterfaces;

    /*public AssetManager assets;
    private ParticleEffect currentEffects;
    private ParticleSystem particleSystem;
    private PointSpriteParticleBatch pointSpriteBatch;*/

    public RenderingSystem(Entities entities) {
        this.entities = entities;

        clearColor = Color.BLACK;
        environment = new Environment();
        shaderProvider = new TiledForwardShaderProvider(environment);

        modelBatches = new Array<>();
        renderingInterfaces = new Array<>();

        /*assets = new AssetManager();
        particleSystem = new ParticleSystem();

        pointSpriteBatch = new PointSpriteParticleBatch();

        particleSystem = new ParticleSystem();
        particleSystem.add(pointSpriteBatch);

        ParticleEffectLoader.ParticleEffectLoadParameter loadParam = new ParticleEffectLoader.ParticleEffectLoadParameter(particleSystem.getBatches());
        ParticleEffectLoader loader = new ParticleEffectLoader(new InternalFileHandleResolver());

        assets.setLoader(ParticleEffect.class, loader);
        assets.load("particles/wheel_smoke.nhfx", ParticleEffect.class, loadParam);

        // halt the main thread until assets are loaded.
        // this is bad for actual games, but okay for demonstration purposes.
        assets.finishLoading();

        currentEffects=assets.get("particles/wheel_smoke.nhfx",ParticleEffect.class).copy();
        currentEffects.init();

        particleSystem.add(currentEffects);*/
    }

    @Override
    protected void processSystem() {
        if (physicsSystem == null) {
            physicsSystem = entities.getEntitySystem(PhysicsSystem.class);
        }

        if (cameraSystem == null) {
            cameraSystem = entities.getEntitySystem(CameraSystem.class);
        }

        if (physicsSystem.isPhysicsInitialized()) {
            if (debugDrawer == null) {
                debugDrawer = new DebugDrawer();
                debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE);
            }

            physicsSystem.setDebugDrawer(debugDrawer);
        }

        cameras = cameraSystem.cameras;

        // Add as many ModelBatches as needed. If there are 5 cameras,
        // 5 model batches are added. Since there are 5 model batches,
        // cameras.size - modelBatches.size is 0, so none are added.
        // So if a camera is added, this will add just 1 model batch.
        for (int i = 0; i < cameras.size - modelBatches.size; i++) {
            modelBatches.add(new ModelBatch(shaderProvider));
        }

        for (int i = 0; i < cameras.size; i++) {
            Camera camera = cameras.get(i);
            ModelBatch modelBatch = modelBatches.get(i);

            GLUtils.clearScreen(clearColor);

            modelBatch.begin(camera);
            for (RenderingSystemInterface rsi : renderingInterfaces) {
                modelBatch.render(rsi.getRenderableProviders(), environment);
            }
            modelBatch.end();

            //pointSpriteBatch.setCamera(camera);

            /*particleSystem.update();
            particleSystem.begin();
            particleSystem.draw();
            particleSystem.end();
            modelBatch.render(particleSystem);*/

            if (Nhg.debugDrawPhysics && debugDrawer != null) {
                debugDrawer.begin(camera);
                physicsSystem.debugDraw();
                debugDrawer.end();
            }
        }
    }

    public void setClearColor(Color clearColor) {
        if (clearColor != null) {
            this.clearColor = clearColor;
        }
    }

    public void addRenderingInterfaces(BaseRenderingSystem... renderingSystems) {
        for (BaseRenderingSystem brs : renderingSystems) {
            renderingInterfaces.add(brs);
        }
    }

    public DebugDrawer getDebugDrawer() {
        return debugDrawer;
    }

    public Environment getEnvironment() {
        return environment;
    }
}
