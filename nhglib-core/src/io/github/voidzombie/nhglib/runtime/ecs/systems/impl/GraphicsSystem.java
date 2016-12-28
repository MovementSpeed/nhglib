package io.github.voidzombie.nhglib.runtime.ecs.systems.impl;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.assets.Asset;
import io.github.voidzombie.nhglib.graphics.interfaces.Representation;
import io.github.voidzombie.nhglib.graphics.utils.DefaultPerspectiveCamera;
import io.github.voidzombie.nhglib.runtime.ecs.components.graphics.GraphicsComponent;
import io.github.voidzombie.nhglib.runtime.ecs.components.scenes.NodeComponent;
import io.github.voidzombie.nhglib.runtime.ecs.systems.base.NHGIteratingSystem;
import io.github.voidzombie.nhglib.runtime.messaging.Message;
import io.github.voidzombie.nhglib.utils.graphics.GLUtils;
import io.reactivex.functions.Consumer;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class GraphicsSystem extends NHGIteratingSystem {
    public DefaultPerspectiveCamera camera;

    private ModelBatch modelBatch;
    private ModelCache dynamicCache;
    private ModelCache staticCache;

    private ComponentMapper<NodeComponent> nodeMapper;
    private ComponentMapper<GraphicsComponent> graphicsMapper;

    public GraphicsSystem() {
        super(Aspect.all(NodeComponent.class, GraphicsComponent.class));

        modelBatch = new ModelBatch();
        dynamicCache = new ModelCache();
        staticCache = new ModelCache();

        camera = new DefaultPerspectiveCamera();
    }

    @Override
    protected void begin() {
        super.begin();
        camera.update();
        dynamicCache.begin(camera);
    }

    @Override
    protected void process(int entityId) {
        GraphicsComponent graphicsComponent = graphicsMapper.get(entityId);
        NodeComponent nodeComponent = nodeMapper.get(entityId);

        Representation representation = graphicsComponent.getRepresentation();

        if (representation != null) {
            RenderableProvider provider = representation.get();

            if (graphicsComponent.type == GraphicsComponent.Type.DYNAMIC) {
                if (provider != null) {
                    representation.setTransform(nodeComponent.getTransform());
                    dynamicCache.add(provider);
                }
            }
        }
    }

    @Override
    protected void end() {
        super.end();
        dynamicCache.end();

        GLUtils.clearScreen(Color.BLACK);

        modelBatch.begin(camera);
        modelBatch.render(staticCache);
        modelBatch.render(dynamicCache);
        modelBatch.end();
    }

    @Override
    protected void inserted(int entityId) {
        super.inserted(entityId);
        final GraphicsComponent graphicsComponent = graphicsMapper.get(entityId);

        NHG.messaging.get(NHG.strings.events.assetLoaded)
                .subscribe(new Consumer<Message>() {
                    @Override
                    public void accept(Message message) throws Exception {
                        if (graphicsComponent.type == GraphicsComponent.Type.STATIC) {
                            Asset asset = (Asset) message.data.get(NHG.strings.defaults.assetKey);

                            if (asset.is(graphicsComponent.asset.alias)) {
                                rebuildCache(graphicsComponent.getRepresentation().get());
                            }
                        }
                    }
                });
    }

    private void rebuildCache(RenderableProvider ... renderableProviders) {
        ModelCache previousModelCache = new ModelCache();

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
