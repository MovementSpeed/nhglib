package io.github.movementspeed.nhglib.runtime.ecs.systems.impl;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.utils.Disposable;
import io.github.movementspeed.nhglib.assets.Asset;
import io.github.movementspeed.nhglib.runtime.ecs.components.graphics.ModelComponent;
import io.github.movementspeed.nhglib.runtime.ecs.components.scenes.NodeComponent;
import io.github.movementspeed.nhglib.runtime.ecs.systems.base.BaseRenderingSystem;
import io.github.movementspeed.nhglib.runtime.ecs.utils.Entities;
import io.github.movementspeed.nhglib.runtime.messaging.Message;
import io.github.movementspeed.nhglib.runtime.messaging.Messaging;
import io.github.movementspeed.nhglib.utils.data.Strings;
import io.reactivex.functions.Consumer;

public class ModelRenderingSystem extends BaseRenderingSystem implements Disposable {
    private Messaging messaging;

    private ComponentMapper<NodeComponent> nodeMapper;
    private ComponentMapper<ModelComponent> modelMapper;

    private ModelCache dynamicCache;
    private ModelCache staticCache;

    public ModelRenderingSystem(Entities entities, Messaging messaging) {
        super(Aspect.all(NodeComponent.class, ModelComponent.class), entities);
        this.messaging = messaging;

        dynamicCache = new ModelCache();
        staticCache = new ModelCache();
        renderableProviders.add(dynamicCache);
        renderableProviders.add(staticCache);
    }

    @Override
    public void dispose() {
        dynamicCache.dispose();
        staticCache.dispose();
    }

    @Override
    protected void begin() {
        super.begin();

        if (cameras.size > 0) {
            Camera camera = cameras.first();
            dynamicCache.begin(camera);
        }
    }

    @Override
    protected void process(int entityId) {
        ModelComponent modelComponent = modelMapper.get(entityId);
        NodeComponent nodeComponent = nodeMapper.get(entityId);

        if (modelComponent.enabled && cameras.size > 0) {
            Camera camera = cameras.first();

            if (camera.frustum.sphereInFrustum(nodeComponent.getTranslation(), modelComponent.radius)) {
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
                    dynamicCache.add(modelComponent.model);
                }
            }
        }
    }

    @Override
    protected void end() {
        super.end();

        if (cameras.size > 0) {
            dynamicCache.end();
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

                            if (asset.is(modelComponent.asset)) {
                                rebuildCache(modelComponent.model);
                            }
                        }
                    }
                });
    }

    private void rebuildCache(RenderableProvider... renderableProviders) {
        if (cameras.size > 0) {
            ModelCache previousCache = new ModelCache();
            Camera camera = cameras.first();

            previousCache.begin(camera);
            previousCache.add(staticCache);
            previousCache.end();

            staticCache.begin(camera);
            staticCache.add(previousCache);

            for (RenderableProvider provider : renderableProviders) {
                staticCache.add(provider);
            }

            staticCache.end();
        }
    }
}
