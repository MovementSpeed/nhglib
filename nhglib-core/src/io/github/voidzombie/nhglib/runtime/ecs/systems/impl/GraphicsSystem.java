package io.github.voidzombie.nhglib.runtime.ecs.systems.impl;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import io.github.voidzombie.nhglib.graphics.representations.ModelRepresentation;
import io.github.voidzombie.nhglib.graphics.utils.DefaultPerspectiveCamera;
import io.github.voidzombie.nhglib.runtime.ecs.components.graphics.GraphicsComponent;
import io.github.voidzombie.nhglib.runtime.ecs.components.scenes.NodeComponent;
import io.github.voidzombie.nhglib.runtime.ecs.systems.base.NhgIteratingSystem;
import io.github.voidzombie.nhglib.runtime.ecs.systems.base.ThreadedIteratingSystem;

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

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(perspectiveCamera);
    }

    @Override
    protected void process(int entityId) {
        NodeComponent nodeComponent = nodeMapper.get(entityId);
        GraphicsComponent graphicsComponent = graphicsMapper.get(entityId);

        graphicsComponent.getRepresentation().setTransform(nodeComponent.getTransform());

        if (graphicsComponent.representation instanceof ModelRepresentation) {
            ModelRepresentation modelRepresentation = graphicsComponent.getRepresentation();
            modelBatch.render(modelRepresentation.get());
        }
    }

    @Override
    protected void end() {
        super.end();
        modelBatch.end();
    }
}
