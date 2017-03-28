package io.github.voidzombie.nhglib.graphics.scenes;

import com.artemis.ComponentMapper;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.utils.Array;
import io.github.voidzombie.nhglib.Nhg;
import io.github.voidzombie.nhglib.assets.Asset;
import io.github.voidzombie.nhglib.data.models.serialization.PbrMaterialJson;
import io.github.voidzombie.nhglib.data.models.serialization.components.*;
import io.github.voidzombie.nhglib.graphics.shaders.attributes.PbrTextureAttribute;
import io.github.voidzombie.nhglib.runtime.ecs.components.graphics.ModelComponent;
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

    private ComponentMapper<ModelComponent> modelMapper;
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

        modelMapper = Nhg.entitySystem.getMapper(ModelComponent.class);
        nodeMapper = Nhg.entitySystem.getMapper(NodeComponent.class);

        SceneUtils.get().addComponentJsonMapping("graphics", GraphicsComponentJson.class);
        SceneUtils.get().addComponentJsonMapping("message", MessageComponentJson.class);
        SceneUtils.get().addComponentJsonMapping("camera", CameraComponentJson.class);
        SceneUtils.get().addComponentJsonMapping("light", LightComponentJson.class);
        SceneUtils.get().addComponentJsonMapping("model", ModelComponentJson.class);

        SceneUtils.get().addAssetClassMapping("model", Model.class);
        SceneUtils.get().addAssetClassMapping("texture", Texture.class);
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
                        if (modelMapper.has(entity)) {
                            loadAssets(entity);
                        }
                    }
                });
    }

    public void unloadScene(final Scene scene) {
        Observable.fromIterable(scene.sceneGraph.getEntities())
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer entity) throws Exception {
                        return modelMapper.has(entity);
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
                        ModelComponent modelComponent = modelMapper.get(integer);
                        Nhg.assets.unloadAsset(modelComponent.asset);
                    }
                });
    }

    public void refresh() {
        loadScene(currentScene);
    }

    public Scene getCurrentScene() {
        return currentScene;
    }

    private void loadAssets(Integer entity) {
        final ModelComponent modelComponent = modelMapper.get(entity);

        // Group all assets
        final Array<Asset> allAssets = new Array<>();
        final Asset modelAsset = modelComponent.asset;

        for (PbrMaterialJson mat : modelComponent.pbrMaterials) {
            allAssets.add(mat.albedoAsset);
            allAssets.add(mat.metalnessAsset);
            allAssets.add(mat.roughnessAsset);
        }

        // Start loading them
        Nhg.assets.queueAsset(modelAsset);

        // Wait for them
        Nhg.messaging.get(Nhg.strings.events.assetLoaded)
                .subscribe(new Consumer<Message>() {
                    @Override
                    public void accept(Message message) throws Exception {
                        Asset asset = (Asset) message.data.get(Nhg.strings.defaults.assetKey);

                        if (asset != null) {
                            if (asset.is(modelAsset.alias)) {
                                Model model = Nhg.assets.get(asset);
                                modelComponent.model = new ModelInstance(model);

                                if (modelComponent.model.animations.size > 0) {
                                    modelComponent.animationController =
                                            new AnimationController(modelComponent.model);
                                }

                                Nhg.assets.queueAssets(allAssets);
                            } else {
                                if (allAssets.contains(asset, false)) {
                                    processMaterialAsset(modelComponent, asset);

                                    allAssets.removeValue(asset, false);
                                    sizeSubject.onNext(allAssets.size);
                                }
                            }
                        }
                    }
                });
    }

    private void processMaterialAsset(ModelComponent modelComponent, Asset asset) {
        Texture texture = Nhg.assets.get(asset);

        for (PbrMaterialJson mat : modelComponent.pbrMaterials) {
            if (asset.is(mat.albedoAsset.alias)) {
                modelComponent.model.materials.get(0)
                        .set(PbrTextureAttribute.createAlbedo(texture));
            } else if (asset.is(mat.metalnessAsset.alias)) {
                modelComponent.model.materials.get(0)
                        .set(PbrTextureAttribute.createMetalness(texture));
            } else if (asset.is(mat.roughnessAsset.alias)) {
                modelComponent.model.materials.get(0)
                        .set(PbrTextureAttribute.createRoughness(texture));
            }
        }
    }
}
