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
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.utils.Array;
import io.github.voidzombie.nhglib.Nhg;
import io.github.voidzombie.nhglib.assets.Asset;
import io.github.voidzombie.nhglib.graphics.shaders.tiledForward.TiledForwardShaderProvider;
import io.github.voidzombie.nhglib.runtime.ecs.components.graphics.ModelComponent;
import io.github.voidzombie.nhglib.runtime.ecs.components.scenes.NodeComponent;
import io.github.voidzombie.nhglib.runtime.ecs.systems.base.NhgIteratingSystem;
import io.github.voidzombie.nhglib.runtime.ecs.utils.Entities;
import io.github.voidzombie.nhglib.runtime.messaging.Message;
import io.github.voidzombie.nhglib.runtime.messaging.Messaging;
import io.github.voidzombie.nhglib.utils.data.Strings;
import io.github.voidzombie.nhglib.utils.graphics.GLUtils;
import io.reactivex.functions.Consumer;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class GraphicsSystem extends NhgIteratingSystem {
    private ShaderProvider shaderProvider;
    private PhysicsSystem physicsSystem;
    private CameraSystem cameraSystem;
    private Environment environment;
    private DebugDrawer debugDrawer;
    private Messaging messaging;
    private Entities entities;
    private Color clearColor;

    private Array<Camera> cameras;
    private Array<ModelBatch> modelBatches;
    private Array<ModelCache> dynamicCaches;
    private Array<ModelCache> staticCaches;

    private ComponentMapper<NodeComponent> nodeMapper;
    private ComponentMapper<ModelComponent> modelMapper;

    public GraphicsSystem(Entities entities, Messaging messaging) {
        super(Aspect.all(NodeComponent.class, ModelComponent.class));
        this.entities = entities;
        this.messaging = messaging;

        clearColor = Color.BLACK;
        environment = new Environment();
        shaderProvider = new TiledForwardShaderProvider(environment);

        modelBatches = new Array<>();
        dynamicCaches = new Array<>();
        staticCaches = new Array<>();
    }

    @Override
    protected void begin() {
        super.begin();

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
        ModelComponent modelComponent = modelMapper.get(entityId);
        NodeComponent nodeComponent = nodeMapper.get(entityId);

        if (modelComponent.enabled) {
            if (modelComponent.animationController != null) {
                modelComponent.animationController.update(Gdx.graphics.getDeltaTime());
            }

            if (modelComponent.type == ModelComponent.Type.DYNAMIC && modelComponent.model != null) {
                if (!modelComponent.nodeAdded) {
                    modelComponent.nodeAdded = true;

                    for (int i = 0; i < modelComponent.model.nodes.size; i++) {
                        Node n = modelComponent.model.nodes.get(i);
                        nodeComponent.node.addChild(n);
                    }

                    String parentInternalNodeId = nodeComponent.parentInternalNodeId;

                    if (parentInternalNodeId != null && !parentInternalNodeId.isEmpty()) {
                        NodeComponent parentNodeComponent = nodeComponent.parentNodeComponent;
                        Node parentInternalNode = parentNodeComponent.node.getChild(parentInternalNodeId, true, false);

                        if (parentInternalNode != null) {
                            parentNodeComponent.node.removeChild(nodeComponent.node);
                            parentInternalNode.addChild(nodeComponent.node);
                        }
                    }
                }

                modelComponent.model.calculateTransforms();

                for (ModelCache modelCache : dynamicCaches) {
                    modelCache.add(modelComponent.model);
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

            GLUtils.clearScreen(clearColor);

            modelBatch.begin(camera);
            modelBatch.render(staticCache, environment);
            modelBatch.render(dynamicCache, environment);
            modelBatch.end();

            if (Nhg.debugDrawPhysics && debugDrawer != null) {
                debugDrawer.begin(camera);
                physicsSystem.debugDraw();
                debugDrawer.end();
            }
        }
    }

    @Override
    protected void inserted(int entityId) {
        super.inserted(entityId);
        final ModelComponent modelComponent = modelMapper.get(entityId);

        messaging.get(Strings.Events.assetLoaded)
                .subscribe(new Consumer<Message>() {
                    @Override
                    public void accept(Message message) throws Exception {
                        if (modelComponent.type == ModelComponent.Type.STATIC) {
                            Asset asset = (Asset) message.data.get(Strings.Defaults.assetKey);

                            if (asset.is(modelComponent.asset.alias)) {
                                rebuildCache(modelComponent.model);
                            }
                        }
                    }
                });
    }

    public void setClearColor(Color clearColor) {
        if (clearColor != null) {
            this.clearColor = clearColor;
        }
    }

    public DebugDrawer getDebugDrawer() {
        return debugDrawer;
    }

    public Environment getEnvironment() {
        return environment;
    }

    private void rebuildCache(RenderableProvider... renderableProviders) {
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
}
