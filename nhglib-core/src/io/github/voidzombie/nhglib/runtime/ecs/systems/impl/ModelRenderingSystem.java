package io.github.voidzombie.nhglib.runtime.ecs.systems.impl;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.utils.Array;
import io.github.voidzombie.nhglib.assets.Asset;
import io.github.voidzombie.nhglib.runtime.ecs.components.graphics.ModelComponent;
import io.github.voidzombie.nhglib.runtime.ecs.components.scenes.NodeComponent;
import io.github.voidzombie.nhglib.runtime.ecs.systems.base.BaseRenderingSystem;
import io.github.voidzombie.nhglib.runtime.ecs.utils.Entities;
import io.github.voidzombie.nhglib.runtime.messaging.Message;
import io.github.voidzombie.nhglib.runtime.messaging.Messaging;
import io.github.voidzombie.nhglib.utils.data.Strings;
import io.reactivex.functions.Consumer;

public class ModelRenderingSystem extends BaseRenderingSystem {
    private CameraSystem cameraSystem;

    private Entities entities;
    private Messaging messaging;

    private ComponentMapper<NodeComponent> nodeMapper;
    private ComponentMapper<ModelComponent> modelMapper;

    private Array<Camera> cameras;
    private Array<ModelCache> dynamicCaches;
    private Array<ModelCache> staticCaches;

    public ModelRenderingSystem(Entities entities, Messaging messaging) {
        super(Aspect.all(NodeComponent.class, ModelComponent.class), entities);

        this.entities = entities;
        this.messaging = messaging;

        dynamicCaches = new Array<>();
        staticCaches = new Array<>();
    }

    @Override
    protected void begin() {
        super.begin();

        if (cameraSystem == null) {
            cameraSystem = entities.getEntitySystem(CameraSystem.class);
        }

        cameras = cameraSystem.cameras;

        for (int i = 0; i < cameras.size - dynamicCaches.size; i++) {
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
        renderableProviders.clear();

        for (int i = 0; i < cameras.size; i++) {
            ModelCache dynamicCache = dynamicCaches.get(i);
            ModelCache staticCache = staticCaches.get(i);

            dynamicCache.end();
            addRenderableProviders(dynamicCache, staticCache);
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
