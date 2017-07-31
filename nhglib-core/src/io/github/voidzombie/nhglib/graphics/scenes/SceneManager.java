package io.github.voidzombie.nhglib.graphics.scenes;

import com.artemis.ComponentMapper;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import io.github.voidzombie.nhglib.assets.Assets;
import io.github.voidzombie.nhglib.data.models.serialization.components.*;
import io.github.voidzombie.nhglib.graphics.shaders.attributes.PbrTextureAttribute;
import io.github.voidzombie.nhglib.graphics.utils.PbrMaterial;
import io.github.voidzombie.nhglib.physics.models.BvhTriangleMeshRigidBodyShape;
import io.github.voidzombie.nhglib.physics.models.ConvexHullRigidBodyShape;
import io.github.voidzombie.nhglib.physics.models.ConvexTriangleMeshRigidBodyShape;
import io.github.voidzombie.nhglib.physics.models.RigidBodyShape;
import io.github.voidzombie.nhglib.runtime.ecs.components.graphics.ModelComponent;
import io.github.voidzombie.nhglib.runtime.ecs.components.physics.RigidBodyComponent;
import io.github.voidzombie.nhglib.runtime.ecs.utils.Entities;
import io.github.voidzombie.nhglib.runtime.messaging.Message;
import io.github.voidzombie.nhglib.runtime.messaging.Messaging;
import io.github.voidzombie.nhglib.utils.data.Strings;
import io.github.voidzombie.nhglib.utils.scenes.SceneUtils;
import io.reactivex.functions.Consumer;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class SceneManager {
    private Scene currentScene;
    private Messaging messaging;
    private Entities entities;
    private Assets assets;

    private ComponentMapper<ModelComponent> modelMapper;
    private ComponentMapper<RigidBodyComponent> rigidBodyMapper;

    public SceneManager(Messaging messaging, Entities entities, Assets assets) {
        this.messaging = messaging;
        this.entities = entities;
        this.assets = assets;

        modelMapper = entities.getMapper(ModelComponent.class);
        rigidBodyMapper = entities.getMapper(RigidBodyComponent.class);

        SceneUtils.addComponentJsonMapping("message", MessageComponentJson.class);
        SceneUtils.addComponentJsonMapping("camera", CameraComponentJson.class);
        SceneUtils.addComponentJsonMapping("light", LightComponentJson.class);
        SceneUtils.addComponentJsonMapping("model", ModelComponentJson.class);
        SceneUtils.addComponentJsonMapping("rigidBody", RigidBodyComponentJson.class);
        SceneUtils.addComponentJsonMapping("vehicle", VehicleComponentJson.class);
        SceneUtils.addComponentJsonMapping("wheel", WheelComponentJson.class);

        SceneUtils.addAssetClassMapping("model", Model.class);
        SceneUtils.addAssetClassMapping("texture", Texture.class);
    }

    public void loadScene(final Scene scene) {
        currentScene = scene;
        assets.queueAssets(scene.assets);

        messaging.get(Strings.Events.assetLoadingFinished)
                .subscribe(new Consumer<Message>() {
                    @Override
                    public void accept(Message message) throws Exception {
                        for (Integer entity : scene.sceneGraph.getEntities()) {
                            processEntityAssets(entity, true);
                        }
                    }
                });
    }

    public void unloadScene(final Scene scene) {
        for (Integer entity : scene.sceneGraph.getEntities()) {
            processEntityAssets(entity, false);
        }
    }

    public void reloadScene() {
        loadScene(currentScene);
    }

    public Scene getCurrentScene() {
        return currentScene;
    }

    private void processEntityAssets(Integer entity, boolean load) {
        // Check if this entity has a model
        if (modelMapper.has(entity)) {
            processModelComponent(entity, load);
        }

        if (rigidBodyMapper.has(entity)) {
            processRigidBodyComponent(entity, load);
        }
    }

    private void processModelComponent(Integer entity, boolean load) {
        ModelComponent modelComponent = modelMapper.get(entity);

        if (load) {
            Model model = assets.get(modelComponent.asset);
            modelComponent.initWithModel(model);

            for (PbrMaterial pbrMaterial : modelComponent.pbrMaterials) {
                Texture albedo = assets.get(pbrMaterial.albedo);
                Texture ambientOcclusion = assets.get(pbrMaterial.ambientOcclusion);
                Texture metalness = assets.get(pbrMaterial.metalness);
                Texture normal = assets.get(pbrMaterial.normal);
                Texture roughness = assets.get(pbrMaterial.roughness);

                if (albedo != null) {
                    pbrMaterial.set(PbrTextureAttribute.createAlbedo(albedo));
                }

                if (ambientOcclusion != null) {
                    pbrMaterial.set(PbrTextureAttribute.createAmbientOcclusion(ambientOcclusion));
                }

                if (metalness != null) {
                    pbrMaterial.set(PbrTextureAttribute.createMetalness(metalness));
                }

                if (normal != null) {
                    pbrMaterial.set(PbrTextureAttribute.createNormal(normal));
                }

                if (roughness != null) {
                    pbrMaterial.set(PbrTextureAttribute.createRoughness(roughness));
                }

                if (pbrMaterial.targetNode != null && !pbrMaterial.targetNode.isEmpty()) {
                    Node targetNode = modelComponent.model.getNode(pbrMaterial.targetNode);

                    for (NodePart nodePart : targetNode.parts) {
                        nodePart.material = pbrMaterial;
                    }
                } else {
                    modelComponent.model.materials.first().set(pbrMaterial);
                }
            }
        } else {
            assets.unloadAsset(modelComponent.asset);

            for (PbrMaterial mat : modelComponent.pbrMaterials) {
                assets.unloadAsset(mat.roughness);
                assets.unloadAsset(mat.normal);
                assets.unloadAsset(mat.metalness);
                assets.unloadAsset(mat.ambientOcclusion);
                assets.unloadAsset(mat.albedo);
            }
        }
    }

    private void processRigidBodyComponent(Integer entity, boolean load) {
        RigidBodyComponent rigidBodyComponent = rigidBodyMapper.get(entity);

        if (load) {
            rigidBodyComponent.build(assets);
            rigidBodyComponent.state = RigidBodyComponent.State.READY;
        } else {
            RigidBodyShape rigidBodyShape = rigidBodyComponent.rigidBodyShape;

            switch (rigidBodyShape.type) {
                case CONVEX_TRIANGLE_MESH:
                    ConvexTriangleMeshRigidBodyShape convexTriangleMeshRigidBodyShape = (ConvexTriangleMeshRigidBodyShape) rigidBodyComponent.rigidBodyShape;
                    assets.unloadAsset(convexTriangleMeshRigidBodyShape.asset);
                    break;

                case BVH_TRIANGLE_MESH:
                    BvhTriangleMeshRigidBodyShape bvhTriangleMeshRigidBodyShape = (BvhTriangleMeshRigidBodyShape) rigidBodyComponent.rigidBodyShape;
                    assets.unloadAsset(bvhTriangleMeshRigidBodyShape.asset);
                    break;

                case CONVEX_HULL:
                    ConvexHullRigidBodyShape convexHullRigidBodyShape = (ConvexHullRigidBodyShape) rigidBodyComponent.rigidBodyShape;
                    assets.unloadAsset(convexHullRigidBodyShape.asset);
                    break;
            }
        }
    }
}
