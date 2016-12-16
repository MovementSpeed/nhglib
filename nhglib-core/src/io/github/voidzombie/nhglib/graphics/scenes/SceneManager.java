package io.github.voidzombie.nhglib.graphics.scenes;

import com.artemis.ComponentMapper;
import com.badlogic.gdx.graphics.g3d.Model;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.assets.Asset;
import io.github.voidzombie.nhglib.graphics.representations.ModelRepresentation;
import io.github.voidzombie.nhglib.runtime.ecs.components.graphics.GraphicsComponent;
import io.reactivex.Observable;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class SceneManager {
    private Scene currentScene;
    private ComponentMapper<GraphicsComponent> graphicsMapper;

    public void loadScene(Scene scene) {
        currentScene = scene;

        if (graphicsMapper == null) {
            graphicsMapper = NHG.entitySystem.getMapper(GraphicsComponent.class);
        }

        Observable.fromIterable(scene.sceneGraph.getEntities())
                .filter(entity -> graphicsMapper.has(entity))
                .subscribe(entity -> {
                    GraphicsComponent graphicsComponent = graphicsMapper.get(entity);

                    if (graphicsComponent.state == GraphicsComponent.State.NOT_INITIALIZED) {
                        loadGraphicsAsset(graphicsComponent);
                    }
                });
    }

    public void refresh() {
        loadScene(currentScene);
    }

    private void loadGraphicsAsset(GraphicsComponent graphicsComponent) {
        graphicsComponent.state = GraphicsComponent.State.LOADING;

        NHG.messaging.get(NHG.strings.events.assetLoaded)
                .filter(message -> {
                    Asset asset = (Asset) message.data.get(NHG.strings.defaults.assetKey);
                    return graphicsComponent.asset.is(asset.alias);
                })
                .subscribe(message -> {
                    Asset asset = (Asset) message.data.get(NHG.strings.defaults.assetKey);
                    createRepresentation(graphicsComponent, asset);
                });

        NHG.assets.queueAsset(graphicsComponent.asset);
    }

    private void createRepresentation(GraphicsComponent graphicsComponent, Asset asset) {
        if (asset.isType(Model.class)) {
            graphicsComponent.setRepresentation(new ModelRepresentation(NHG.assets.get(asset)));
        }
    }
}
