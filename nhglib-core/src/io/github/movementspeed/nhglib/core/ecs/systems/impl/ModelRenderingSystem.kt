package io.github.movementspeed.nhglib.core.ecs.systems.impl;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.model.Node;
import io.github.movementspeed.nhglib.assets.Assets;
import io.github.movementspeed.nhglib.core.ecs.components.graphics.ModelComponent;
import io.github.movementspeed.nhglib.core.ecs.components.scenes.NodeComponent;
import io.github.movementspeed.nhglib.core.ecs.systems.base.BaseRenderingSystem;
import io.github.movementspeed.nhglib.core.ecs.utils.Entities;
import io.github.movementspeed.nhglib.core.messaging.Messaging;
import io.github.movementspeed.nhglib.utils.debug.NhgLogger;

public class ModelRenderingSystem extends BaseRenderingSystem {
    private boolean buildingModelCache = false;
    private Messaging messaging;
    private Assets assets;
    private ModelCache staticCache;

    private ComponentMapper<NodeComponent> nodeMapper;
    private ComponentMapper<ModelComponent> modelMapper;

    public ModelRenderingSystem(Entities entities, Messaging messaging, Assets assets) {
        super(Aspect.all(NodeComponent.class, ModelComponent.class), entities);
        this.messaging = messaging;
        this.assets = assets;
        staticCache = new ModelCache();
    }

    @Override
    protected void begin() {
        super.begin();

        if (!buildingModelCache) renderableProviders.add(staticCache);
    }

    @Override
    protected void process(int entityId) {
        ModelComponent modelComponent = modelMapper.get(entityId);
        NodeComponent nodeComponent = nodeMapper.get(entityId);

        if (modelComponent.enabled &&
                cameras.size > 0 &&
                modelComponent.state == ModelComponent.State.READY) {

            Camera camera = cameras.first();

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

            modelComponent.calculateTransforms();

            if (modelComponent.type == ModelComponent.Type.DYNAMIC) {
                if (camera.frustum.sphereInFrustum(nodeComponent.getTranslation(), modelComponent.radius)) {
                    if (modelComponent.animationController != null) {
                        modelComponent.animationController.update(Gdx.graphics.getDeltaTime());
                    }

                    renderableProviders.add(modelComponent.model);
                }
            } else if (modelComponent.type == ModelComponent.Type.STATIC) {
                if (!modelComponent.cached) {
                    modelComponent.cached = true;
                    rebuildCache(modelComponent.model);
                }
            }
        }
    }

    @Override
    protected void dispose() {
        super.dispose();
        staticCache.dispose();
    }

    private void rebuildCache(RenderableProvider... renderableProviders) {
        if (cameras.size > 0) {
            NhgLogger.log(this, "Rebuilding model cache.");
            buildingModelCache = true;
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
            previousCache.dispose();
            buildingModelCache = false;
        }
    }
}
