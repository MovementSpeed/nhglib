package io.github.voidzombie.nhglib.runtime.ecs.systems.impl;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.utils.Array;
import io.github.voidzombie.nhglib.Nhg;
import io.github.voidzombie.nhglib.assets.Asset;
import io.github.voidzombie.nhglib.graphics.interfaces.Representation;
import io.github.voidzombie.nhglib.graphics.representations.ModelRepresentation;
import io.github.voidzombie.nhglib.runtime.ecs.components.graphics.GraphicsComponent;
import io.github.voidzombie.nhglib.runtime.ecs.components.scenes.NodeComponent;
import io.github.voidzombie.nhglib.runtime.ecs.systems.base.NhgIteratingSystem;
import io.github.voidzombie.nhglib.runtime.messaging.Message;
import io.github.voidzombie.nhglib.utils.graphics.GLUtils;
import io.reactivex.functions.Consumer;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class GraphicsSystem extends NhgIteratingSystem {
    private ShaderProvider shaderProvider;
    private CameraSystem cameraSystem;
    private Environment environment;

    private Array<Camera> cameras;
    private Array<ModelBatch> modelBatches;
    private Array<ModelCache> dynamicCaches;
    private Array<ModelCache> staticCaches;

    private ComponentMapper<NodeComponent> nodeMapper;
    private ComponentMapper<GraphicsComponent> graphicsMapper;

    public GraphicsSystem() {
        super(Aspect.all(NodeComponent.class, GraphicsComponent.class));

        environment = new Environment();
        shaderProvider = new DefaultShaderProvider();

        modelBatches = new Array<>();
        dynamicCaches = new Array<>();
        staticCaches = new Array<>();
    }

    @Override
    protected void begin() {
        super.begin();

        if (cameraSystem == null) {
            cameraSystem = Nhg.entitySystem.getEntitySystem(CameraSystem.class);
        }

        cameras = cameraSystem.cameras;

        for (int i = 0; i < cameras.size - modelBatches.size; i++) {
            modelBatches.add(new ModelBatch(shaderProvider));
            dynamicCaches.add(new ModelCache());
            staticCaches.add(new ModelCache());
        }

        for (int i = 0; i < cameras.size; i++) {
            Camera camera = cameras.get(i);

            ModelCache dynamicCache = dynamicCaches.get(i);
            dynamicCache.begin(camera);
        }
    }

    @Override
    protected void process(int entityId) {
        GraphicsComponent graphicsComponent = graphicsMapper.get(entityId);
        NodeComponent nodeComponent = nodeMapper.get(entityId);

        Representation representation = graphicsComponent.getRepresentation();
        processSpecifics(representation);

        if (representation != null) {
            RenderableProvider provider = representation.get();

            if (graphicsComponent.type == GraphicsComponent.Type.DYNAMIC) {
                if (provider != null) {
                    representation.setTransform(nodeComponent.getTransform());

                    for (ModelCache modelCache : dynamicCaches) {
                        modelCache.add(provider);
                    }
                }
            }
        }
    }

    @Override
    protected void end() {
        super.end();

        for (int i = 0; i < cameras.size; i++) {
            Camera camera = cameras.get(i);
            ModelBatch modelBatch = modelBatches.get(i);
            ModelCache dynamicCache = dynamicCaches.get(i);
            ModelCache staticCache = staticCaches.get(i);

            dynamicCache.end();

            GLUtils.clearScreen(Color.BLACK);

            modelBatch.begin(camera);
            modelBatch.render(staticCache, environment);
            modelBatch.render(dynamicCache, environment);
            modelBatch.end();
        }
    }

    @Override
    protected void inserted(int entityId) {
        super.inserted(entityId);
        final GraphicsComponent graphicsComponent = graphicsMapper.get(entityId);

        Nhg.messaging.get(Nhg.strings.events.assetLoaded)
                .subscribe(new Consumer<Message>() {
                    @Override
                    public void accept(Message message) throws Exception {
                        if (graphicsComponent.type == GraphicsComponent.Type.STATIC) {
                            Asset asset = (Asset) message.data.get(Nhg.strings.defaults.assetKey);

                            if (asset.is(graphicsComponent.asset.alias)) {
                                rebuildCache(graphicsComponent.getRepresentation().get());
                            }
                        }
                    }
                });
    }

    public void setShaderProvider(ShaderProvider provider) {
        this.shaderProvider = provider;
    }

    public Environment getEnvironment() {
        return environment;
    }

    private void rebuildCache(RenderableProvider ... renderableProviders) {
        Array<ModelCache> previousModelCaches = new Array<>(staticCaches);

        for (int i = 0; i < cameras.size; i++) {
            ModelCache previousModelCache = previousModelCaches.get(i);
            ModelCache staticCache = staticCaches.get(i);
            Camera camera = cameras.get(i);

            previousModelCache.begin(camera);
            previousModelCache.add(staticCache);
            previousModelCache.end();

            staticCache.begin(camera);
            staticCache.add(previousModelCache);

            for (RenderableProvider provider : renderableProviders) {
                staticCache.add(provider);
            }

            staticCache.end();
        }
    }

    private void processSpecifics(Representation representation) {
        if (representation instanceof ModelRepresentation) {
            ModelRepresentation modelRepresentation = ((ModelRepresentation) representation);

            if (modelRepresentation.getAnimationController() != null) {
                modelRepresentation.getAnimationController().update(Gdx.graphics.getDeltaTime());
            }
        }
    }
}
