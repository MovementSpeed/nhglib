package io.github.voidzombie.nhglib.graphics.scenes;

import com.artemis.ComponentMapper;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.utils.Array;
import io.github.voidzombie.nhglib.Nhg;
import io.github.voidzombie.nhglib.assets.Asset;
import io.github.voidzombie.nhglib.data.models.serialization.components.*;
import io.github.voidzombie.nhglib.graphics.shaders.attributes.PbrTextureAttribute;
import io.github.voidzombie.nhglib.graphics.utils.PbrMaterial;
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

    private Subject<Bundle> sizeSubject;
    private Array<Asset> assetsToLoad;
    private Array<Asset> temporaryLoadedAssets;

    public SceneManager() {
        sizeSubject = PublishSubject.create();
        sizeSubject.subscribe(new Consumer<Bundle>() {
            @Override
            public void accept(Bundle bundle) throws Exception {
                if (bundle.getInteger("size") == 0) {
                    ModelComponent modelComponent = (ModelComponent) bundle.get("modelComponent");
                    processMaterialAssets(modelComponent);

                    Bundle sceneBundle = new Bundle();
                    sceneBundle.put(Nhg.strings.defaults.sceneKey, currentScene);

                    Message message = new Message(Nhg.strings.events.sceneLoaded, sceneBundle);
                    Nhg.messaging.send(message);
                }
            }
        });

        assetsToLoad = new Array<>();
        temporaryLoadedAssets = new Array<>();

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

        for (PbrMaterial mat : modelComponent.pbrMaterials) {
            if (mat.albedoAsset != null) {
                allAssets.add(mat.albedoAsset);
            }

            if (mat.metalnessAsset != null) {
                allAssets.add(mat.metalnessAsset);
            }

            if (mat.roughnessAsset != null) {
                allAssets.add(mat.roughnessAsset);
            }

            if (mat.normalAsset != null) {
                allAssets.add(mat.normalAsset);
            }

            if (mat.ambientOcclusionAsset != null) {
                allAssets.add(mat.ambientOcclusionAsset);
            }
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
                                    temporaryLoadedAssets.add(asset);
                                    allAssets.removeValue(asset, false);

                                    Bundle bundle = new Bundle();
                                    bundle.put("size", allAssets.size);
                                    bundle.put("modelComponent", modelComponent);

                                    sizeSubject.onNext(bundle);
                                }
                            }
                        }
                    }
                });
    }

    private void processMaterialAssets(ModelComponent modelComponent) {
        for (Asset asset : temporaryLoadedAssets) {
            Texture texture = Nhg.assets.get(asset);

            for (PbrMaterial pbrMat : modelComponent.pbrMaterials) {
                if (asset.is(pbrMat.albedoAsset.alias)) {
                    pbrMat.set(PbrTextureAttribute.createAlbedo(texture));
                } else if (asset.is(pbrMat.metalnessAsset.alias)) {
                    pbrMat.set(PbrTextureAttribute.createMetalness(texture));
                } else if (asset.is(pbrMat.roughnessAsset.alias)) {
                    pbrMat.set(PbrTextureAttribute.createRoughness(texture));
                } else if (asset.is(pbrMat.normalAsset.alias)) {
                    pbrMat.set(PbrTextureAttribute.createNormal(texture));
                } else if (asset.is(pbrMat.ambientOcclusionAsset.alias)) {
                    pbrMat.set(PbrTextureAttribute.createAmbientOcclusion(texture));
                }
            }
        }

        for (PbrMaterial pbrMat : modelComponent.pbrMaterials) {
            Node targetNode = modelComponent.model.getNode(pbrMat.targetNode);

            for (NodePart nodePart : targetNode.parts) {
                nodePart.material = pbrMat;
            }
        }
    }
}
