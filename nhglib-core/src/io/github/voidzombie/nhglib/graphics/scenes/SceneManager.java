package io.github.voidzombie.nhglib.graphics.scenes;

import com.artemis.ComponentMapper;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.assets.Asset;
import io.github.voidzombie.nhglib.data.models.components.GraphicsComponentJson;
import io.github.voidzombie.nhglib.data.models.components.MessageComponentJson;
import io.github.voidzombie.nhglib.graphics.representations.ModelRepresentation;
import io.github.voidzombie.nhglib.runtime.ecs.components.graphics.GraphicsComponent;
import io.github.voidzombie.nhglib.runtime.ecs.components.scenes.NodeComponent;
import io.github.voidzombie.nhglib.runtime.messaging.Message;
import io.github.voidzombie.nhglib.utils.scenes.SceneUtils;
import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class SceneManager {
    private Scene currentScene;
    private ComponentMapper<GraphicsComponent> graphicsMapper;
    private ComponentMapper<NodeComponent> nodeMapper;

    public SceneManager() {
        graphicsMapper = NHG.entitySystem.getMapper(GraphicsComponent.class);
        nodeMapper = NHG.entitySystem.getMapper(NodeComponent.class);

        SceneUtils.getInstance().addComponentJsonMapping("graphics", GraphicsComponentJson.class);
        SceneUtils.getInstance().addComponentJsonMapping("message", MessageComponentJson.class);
        SceneUtils.getInstance().addAssetClassMapping("model", Model.class);
    }

    public void loadScene(Scene scene) {
        currentScene = scene;

        Observable.fromIterable(scene.sceneGraph.getEntities())
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer entity) throws Exception {
                        return graphicsMapper.has(entity);
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        GraphicsComponent graphicsComponent = graphicsMapper.get(integer);

                        if (graphicsComponent.state == GraphicsComponent.State.NOT_INITIALIZED) {
                            loadGraphicsAsset(graphicsComponent);
                        }
                    }
                });
    }

    public void unloadScene(final Scene scene) {
        Observable.fromIterable(scene.sceneGraph.getEntities())
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer entity) throws Exception {
                        return graphicsMapper.has(entity);
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        int rootEntity = scene.sceneGraph.getRootEntity();
                        NodeComponent nodeComponent = nodeMapper.get(rootEntity);

                        nodeComponent.setTranslation(0, 0, 0);
                        nodeComponent.setRotation(0, 0, 0);
                        nodeComponent.setScale(1, 1, 1);
                        nodeComponent.applyTransforms();
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        GraphicsComponent graphicsComponent = graphicsMapper.get(integer);
                        graphicsComponent.invalidate();

                        NHG.assets.unloadAsset(graphicsComponent.asset);
                    }
                });
    }

    public void refresh() {
        loadScene(currentScene);
    }

    public Scene getCurrentScene() {
        return currentScene;
    }

    private void loadGraphicsAsset(final GraphicsComponent graphicsComponent) {
        graphicsComponent.state = GraphicsComponent.State.LOADING;

        NHG.messaging.get(NHG.strings.events.assetLoaded)
                .filter(new Predicate<Message>() {
                    @Override
                    public boolean test(Message message) throws Exception {
                        Asset asset = (Asset) message.data.get(NHG.strings.defaults.assetKey);
                        return graphicsComponent.asset.is(asset.alias);
                    }
                })
                .subscribe(new Consumer<Message>() {
                    @Override
                    public void accept(Message message) throws Exception {
                        Asset asset = (Asset) message.data.get(NHG.strings.defaults.assetKey);
                        createRepresentation(graphicsComponent, asset);
                    }
                });

        NHG.assets.queueAsset(graphicsComponent.asset);
    }

    private void createRepresentation(GraphicsComponent graphicsComponent, Asset asset) {
        if (asset.isType(Model.class)) {
            Model model = NHG.assets.get(asset);
            ModelRepresentation representation = new ModelRepresentation(model);
            graphicsComponent.setRepresentation(representation);
        }
    }
}
