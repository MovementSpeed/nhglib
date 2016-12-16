package io.github.voidzombie.nhglib.runtime.ecs.systems.impl;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import io.github.voidzombie.nhglib.graphics.representations.ModelRepresentation;
import io.github.voidzombie.nhglib.graphics.utils.DefaultPerspectiveCamera;
import io.github.voidzombie.nhglib.runtime.ecs.components.graphics.GraphicsComponent;
import io.github.voidzombie.nhglib.runtime.ecs.components.scenes.NodeComponent;
import io.github.voidzombie.nhglib.runtime.ecs.systems.base.NhgIteratingSystem;
import io.github.voidzombie.nhglib.utils.graphics.GLUtils;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class GraphicsSystem extends NhgIteratingSystem {
    private ComponentMapper<NodeComponent> nodeMapper;
    private ComponentMapper<GraphicsComponent> graphicsMapper;

    private ModelBatch modelBatch;
    private DefaultPerspectiveCamera perspectiveCamera;

    public GraphicsSystem() {
        super(Aspect.all(NodeComponent.class, GraphicsComponent.class));

        modelBatch = new ModelBatch();
        perspectiveCamera = new DefaultPerspectiveCamera();
    }

    @Override
    protected void begin() {
        super.begin();
        perspectiveCamera.update();

        GLUtils.clearScreen(Color.WHITE);
        modelBatch.begin(perspectiveCamera);
    }

    @Override
    protected void process(int entityId) {
        NodeComponent nodeComponent = nodeMapper.get(entityId);
        GraphicsComponent graphicsComponent = graphicsMapper.get(entityId);

        if (graphicsComponent.getRepresentation() != null) {
            RenderableProvider provider = getRenderableProvider(graphicsComponent);

            if (provider != null) {
                graphicsComponent.getRepresentation().setTransform(nodeComponent.getTransform());
                modelBatch.render(provider);
            }
        }
    }

    @Override
    protected void end() {
        super.end();
        modelBatch.end();
    }

    private RenderableProvider getRenderableProvider(GraphicsComponent graphicsComponent) {
        RenderableProvider provider = null;

        if (graphicsComponent.getRepresentation() instanceof ModelRepresentation) {
            ModelRepresentation modelRepresentation = graphicsComponent.getRepresentation();
            provider = modelRepresentation.get();
        }

        return provider;
    }
}
