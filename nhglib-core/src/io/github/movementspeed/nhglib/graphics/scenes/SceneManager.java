package io.github.movementspeed.nhglib.graphics.scenes;

import com.artemis.ComponentMapper;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader;
import com.badlogic.gdx.utils.Array;
import io.github.movementspeed.nhglib.assets.Asset;
import io.github.movementspeed.nhglib.assets.Assets;
import io.github.movementspeed.nhglib.data.models.serialization.components.*;
import io.github.movementspeed.nhglib.files.HDRData;
import io.github.movementspeed.nhglib.graphics.shaders.attributes.PbrTextureAttribute;
import io.github.movementspeed.nhglib.graphics.utils.PbrMaterial;
import io.github.movementspeed.nhglib.physics.models.BvhTriangleMeshRigidBodyShape;
import io.github.movementspeed.nhglib.physics.models.ConvexHullRigidBodyShape;
import io.github.movementspeed.nhglib.physics.models.ConvexTriangleMeshRigidBodyShape;
import io.github.movementspeed.nhglib.physics.models.RigidBodyShape;
import io.github.movementspeed.nhglib.runtime.ecs.components.graphics.ModelComponent;
import io.github.movementspeed.nhglib.runtime.ecs.components.graphics.ParticleEffectComponent;
import io.github.movementspeed.nhglib.runtime.ecs.components.physics.RigidBodyComponent;
import io.github.movementspeed.nhglib.runtime.ecs.components.physics.VehicleComponent;
import io.github.movementspeed.nhglib.runtime.ecs.components.physics.WheelComponent;
import io.github.movementspeed.nhglib.runtime.ecs.systems.impl.ParticleRenderingSystem;
import io.github.movementspeed.nhglib.runtime.ecs.systems.impl.PhysicsSystem;
import io.github.movementspeed.nhglib.runtime.ecs.utils.Entities;
import io.github.movementspeed.nhglib.runtime.messaging.Message;
import io.github.movementspeed.nhglib.runtime.messaging.Messaging;
import io.github.movementspeed.nhglib.utils.data.Bundle;
import io.github.movementspeed.nhglib.utils.data.Strings;
import io.github.movementspeed.nhglib.utils.scenes.SceneUtils;
import io.reactivex.functions.Consumer;

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
public class SceneManager {
    private int assetsToLoad;

    private Scene currentScene;
    private Messaging messaging;
    private Entities entities;
    private Assets assets;

    private ComponentMapper<ModelComponent> modelMapper;
    private ComponentMapper<RigidBodyComponent> rigidBodyMapper;
    private ComponentMapper<VehicleComponent> vehicleMapper;
    private ComponentMapper<WheelComponent> vehicleWheelMapper;
    private ComponentMapper<ParticleEffectComponent> particleEffectMapper;

    public SceneManager(Messaging messaging, Entities entities, Assets assets) {
        this.messaging = messaging;
        this.entities = entities;
        this.assets = assets;

        modelMapper = entities.getMapper(ModelComponent.class);
        rigidBodyMapper = entities.getMapper(RigidBodyComponent.class);
        vehicleMapper = entities.getMapper(VehicleComponent.class);
        vehicleWheelMapper = entities.getMapper(WheelComponent.class);
        particleEffectMapper = entities.getMapper(ParticleEffectComponent.class);

        SceneUtils.addComponentJsonMapping("message", MessageComponentJson.class);
        SceneUtils.addComponentJsonMapping("camera", CameraComponentJson.class);
        SceneUtils.addComponentJsonMapping("light", LightComponentJson.class);
        SceneUtils.addComponentJsonMapping("model", ModelComponentJson.class);
        SceneUtils.addComponentJsonMapping("rigidBody", RigidBodyComponentJson.class);
        SceneUtils.addComponentJsonMapping("vehicle", VehicleComponentJson.class);
        SceneUtils.addComponentJsonMapping("wheel", WheelComponentJson.class);
        SceneUtils.addComponentJsonMapping("particleEffect", ParticleEffectComponentJson.class);

        SceneUtils.addAssetClassMapping("model", Model.class);
        SceneUtils.addAssetClassMapping("texture", Texture.class);
        SceneUtils.addAssetClassMapping("particleEffect", ParticleEffect.class);
        SceneUtils.addAssetClassMapping("hdr", HDRData.class);
    }

    public void loadScene(final Scene scene) {
        currentScene = scene;

        processAssets(scene.assets);
        assets.queueAssets(scene.assets);

        assetsToLoad = scene.assets.size;

        messaging.get(Strings.Events.assetLoadingFinished, Strings.Events.assetLoaded)
                .subscribe(new Consumer<Message>() {
                    @Override
                    public void accept(Message message) throws Exception {
                        if (message.is(Strings.Events.assetLoadingFinished)) {
                            for (int entity : scene.sceneGraph.getEntities()) {
                                processEntityAssets(entity, true);
                            }
                        } else if (message.is(Strings.Events.assetLoaded)) {
                            Asset asset = (Asset) message.data.get(Strings.Defaults.assetKey);

                            if (scene.assets.contains(asset, true)) {
                                assetsToLoad--;
                            }

                            if (assetsToLoad == 0) {
                                Bundle messageBundle = new Bundle();
                                messageBundle.put(Strings.Defaults.sceneKey, scene);
                                messaging.send(new Message(Strings.Events.sceneLoaded, messageBundle));
                            }
                        }
                    }
                });
    }

    public void unloadScene(final Scene scene) {
        for (int entity : scene.sceneGraph.getEntities()) {
            processEntityAssets(entity, false);
        }
    }

    public void reloadScene() {
        loadScene(currentScene);
    }

    public Scene getCurrentScene() {
        return currentScene;
    }

    private void processAssets(Array<Asset> assets) {
        for (Asset asset : assets) {
            if (asset.isType(ParticleEffect.class)) {
                ParticleRenderingSystem particleRenderingSystem = entities.getEntitySystem(ParticleRenderingSystem.class);
                asset.parameters = new ParticleEffectLoader.ParticleEffectLoadParameter(particleRenderingSystem.getParticleSystem().getBatches());
            }
        }
    }

    private void processEntityAssets(int entity, boolean load) {
        // Check if this entity has a model
        if (modelMapper.has(entity)) {
            processModelComponent(entity, load);
        }

        // Check if this entity has a rigid body
        if (rigidBodyMapper.has(entity)) {
            processRigidBodyComponent(entity, load);
        }

        // Check if this entity is a vehicle
        if (vehicleMapper.has(entity)) {
            processVehicleComponent(entity, load);
        }

        // Check if this entity is a wheel
        if (vehicleWheelMapper.has(entity)) {
            processWheelComponent(entity);
        }

        // Check if this entity has a particle effect
        if (particleEffectMapper.has(entity)) {
            processParticleEffectComponent(entity, load);
        }
    }

    private void processModelComponent(int entity, boolean load) {
        ModelComponent modelComponent = modelMapper.get(entity);

        if (load) {
            Model model = assets.get(modelComponent.asset);
            modelComponent.initWithModel(model);

            for (PbrMaterial pbrMaterial : modelComponent.pbrMaterials) {
                if (!pbrMaterial.albedo.isEmpty()) {
                    Texture albedo = assets.get(pbrMaterial.albedo);

                    if (albedo != null) {
                        pbrMaterial.set(PbrTextureAttribute.createAlbedo(albedo));
                    }
                }

                if (!pbrMaterial.ambientOcclusion.isEmpty()) {
                    Texture ambientOcclusion = assets.get(pbrMaterial.ambientOcclusion);

                    if (ambientOcclusion != null) {
                        pbrMaterial.set(PbrTextureAttribute.createAmbientOcclusion(ambientOcclusion));
                    }
                }

                if (!pbrMaterial.metalness.isEmpty()) {
                    Texture metalness = assets.get(pbrMaterial.metalness);

                    if (metalness != null) {
                        pbrMaterial.set(PbrTextureAttribute.createMetalness(metalness));
                    }
                }

                if (!pbrMaterial.normal.isEmpty()) {
                    Texture normal = assets.get(pbrMaterial.normal);

                    if (normal != null) {
                        pbrMaterial.set(PbrTextureAttribute.createNormal(normal));
                    }
                }

                if (!pbrMaterial.roughness.isEmpty()) {
                    Texture roughness = assets.get(pbrMaterial.roughness);

                    if (roughness != null) {
                        pbrMaterial.set(PbrTextureAttribute.createRoughness(roughness));
                    }
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

    private void processRigidBodyComponent(int entity, boolean load) {
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

    private void processVehicleComponent(int entity, boolean load) {
        VehicleComponent vehicleComponent = vehicleMapper.get(entity);

        if (load) {
            PhysicsSystem physicsSystem = entities.getEntitySystem(PhysicsSystem.class);
            vehicleComponent.build(assets, physicsSystem.getBulletWorld());
            vehicleComponent.state = RigidBodyComponent.State.READY;
        } else {
            RigidBodyShape rigidBodyShape = vehicleComponent.rigidBodyShape;

            switch (rigidBodyShape.type) {
                case CONVEX_TRIANGLE_MESH:
                    ConvexTriangleMeshRigidBodyShape convexTriangleMeshRigidBodyShape = (ConvexTriangleMeshRigidBodyShape) vehicleComponent.rigidBodyShape;
                    assets.unloadAsset(convexTriangleMeshRigidBodyShape.asset);
                    break;

                case BVH_TRIANGLE_MESH:
                    BvhTriangleMeshRigidBodyShape bvhTriangleMeshRigidBodyShape = (BvhTriangleMeshRigidBodyShape) vehicleComponent.rigidBodyShape;
                    assets.unloadAsset(bvhTriangleMeshRigidBodyShape.asset);
                    break;

                case CONVEX_HULL:
                    ConvexHullRigidBodyShape convexHullRigidBodyShape = (ConvexHullRigidBodyShape) vehicleComponent.rigidBodyShape;
                    assets.unloadAsset(convexHullRigidBodyShape.asset);
                    break;
            }
        }
    }

    private void processWheelComponent(int entity) {
        WheelComponent wheelComponent = vehicleWheelMapper.get(entity);
        wheelComponent.build();
        wheelComponent.state = WheelComponent.State.READY;
    }

    private void processParticleEffectComponent(int entity, boolean load) {
        ParticleEffectComponent particleEffectComponent = particleEffectMapper.get(entity);

        if (load) {
            ParticleRenderingSystem particleRenderingSystem = entities.getEntitySystem(ParticleRenderingSystem.class);
            particleEffectComponent.build(assets, particleRenderingSystem.getParticleEffectProvider());
            particleEffectComponent.state = ParticleEffectComponent.State.READY;
        } else {
            assets.unloadAsset(particleEffectComponent.asset);
        }
    }
}
