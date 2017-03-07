package io.github.voidzombie.nhglib.graphics.scenes;

import com.artemis.ComponentMapper;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.utils.Array;
import io.github.voidzombie.nhglib.Nhg;
import io.github.voidzombie.nhglib.assets.Asset;
import io.github.voidzombie.nhglib.data.models.serialization.components.CameraComponentJson;
import io.github.voidzombie.nhglib.data.models.serialization.components.GraphicsComponentJson;
import io.github.voidzombie.nhglib.data.models.serialization.components.MessageComponentJson;
import io.github.voidzombie.nhglib.graphics.representations.ModelRepresentation;
import io.github.voidzombie.nhglib.runtime.ecs.components.graphics.GraphicsComponent;
import io.github.voidzombie.nhglib.runtime.ecs.components.scenes.NodeComponent;
import io.github.voidzombie.nhglib.runtime.messaging.Message;
import io.github.voidzombie.nhglib.utils.data.Bundle;
import io.github.voidzombie.nhglib.utils.scenes.SceneUtils;
import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class SceneManager {
    private Scene currentScene;

    private ComponentMapper<GraphicsComponent> graphicsMapper;
    private ComponentMapper<NodeComponent> nodeMapper;

    private Subject<Integer> sizeSubject;
    private Array<Asset> assetsToLoad;

    public SceneManager() {
        sizeSubject = PublishSubject.create();
        sizeSubject.subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                if (integer == 0) {
                    Bundle bundle = new Bundle();
                    bundle.put(Nhg.strings.defaults.sceneKey, currentScene);

                    Message message = new Message(Nhg.strings.events.sceneLoaded, bundle);
                    Nhg.messaging.send(message);
                }
            }
        });

        assetsToLoad = new Array<>();

        graphicsMapper = Nhg.entitySystem.getMapper(GraphicsComponent.class);
        nodeMapper = Nhg.entitySystem.getMapper(NodeComponent.class);

        SceneUtils.get().addComponentJsonMapping("graphics", GraphicsComponentJson.class);
        SceneUtils.get().addComponentJsonMapping("message", MessageComponentJson.class);
        SceneUtils.get().addComponentJsonMapping("camera", CameraComponentJson.class);
        SceneUtils.get().addAssetClassMapping("model", Model.class);
    }

    public void loadScene(Scene scene) {
        assetsToLoad.clear();
        currentScene = scene;

        Observable.fromIterable(scene.sceneGraph.getEntities())
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        Nhg.assets.queueAssets(assetsToLoad);
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer entity) throws Exception {
                        if (graphicsMapper.has(entity)) {
                            fetchAssets(entity);
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

                        Nhg.assets.unloadAsset(graphicsComponent.asset);
                    }
                });
    }

    public void refresh() {
        loadScene(currentScene);
    }

    public Scene getCurrentScene() {
        return currentScene;
    }

    private void fetchAssets(Integer entity) {
        final GraphicsComponent graphicsComponent = graphicsMapper.get(entity);

        if (graphicsComponent.state == GraphicsComponent.State.NOT_INITIALIZED) {
            graphicsComponent.state = GraphicsComponent.State.LOADING;

            Nhg.messaging.get(Nhg.strings.events.assetLoaded)
                    .filter(new Predicate<Message>() {
                        @Override
                        public boolean test(Message message) throws Exception {
                            Asset asset = (Asset) message.data.get(Nhg.strings.defaults.assetKey);
                            return graphicsComponent.asset.is(asset.alias);
                        }
                    })
                    .subscribe(new Consumer<Message>() {
                        @Override
                        public void accept(Message message) throws Exception {
                            Asset asset = (Asset) message.data.get(Nhg.strings.defaults.assetKey);
                            createRepresentation(graphicsComponent, asset);
                            assetsToLoad.removeValue(graphicsComponent.asset, true);
                            sizeSubject.onNext(assetsToLoad.size);
                        }
                    });

            if (assetsToLoad.contains(graphicsComponent.asset, false)) {
                Nhg.messaging.send(new Message(Nhg.strings.events.engineDestroy));
                throw new RuntimeException("A scene cannot contain multiple assets with the same alias.");
            } else {
                assetsToLoad.add(graphicsComponent.asset);
            }
        }
    }

    private void createRepresentation(GraphicsComponent graphicsComponent, Asset asset) {
        if (asset.isType(Model.class)) {
            Model model = Nhg.assets.get(asset);
            ModelRepresentation representation = new ModelRepresentation(model);
            graphicsComponent.setRepresentation(representation);
        }
    }
}
