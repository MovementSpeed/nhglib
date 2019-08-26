package io.github.movementspeed.nhglib.graphics.scenes

import com.artemis.ComponentMapper
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.graphics.g3d.model.Node
import com.badlogic.gdx.graphics.g3d.model.NodePart
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Array
import io.github.movementspeed.nhglib.assets.Asset
import io.github.movementspeed.nhglib.assets.AssetPackage
import io.github.movementspeed.nhglib.assets.Assets
import io.github.movementspeed.nhglib.core.ecs.components.graphics.ModelComponent
import io.github.movementspeed.nhglib.core.ecs.components.graphics.ParticleEffectComponent
import io.github.movementspeed.nhglib.core.ecs.components.physics.RigidBodyComponent
import io.github.movementspeed.nhglib.core.ecs.components.physics.VehicleComponent
import io.github.movementspeed.nhglib.core.ecs.components.physics.TyreComponent
import io.github.movementspeed.nhglib.core.ecs.components.scenes.NodeComponent
import io.github.movementspeed.nhglib.core.ecs.systems.impl.ParticleRenderingSystem
import io.github.movementspeed.nhglib.core.ecs.systems.impl.PhysicsSystem
import io.github.movementspeed.nhglib.core.ecs.utils.Entities
import io.github.movementspeed.nhglib.core.messaging.Message
import io.github.movementspeed.nhglib.core.messaging.Messaging
import io.github.movementspeed.nhglib.data.models.serialization.components.*
import io.github.movementspeed.nhglib.files.HDRData
import io.github.movementspeed.nhglib.graphics.shaders.attributes.PBRTextureAttribute
import io.github.movementspeed.nhglib.graphics.utils.PBRMaterial
import io.github.movementspeed.nhglib.physics.models.BvhTriangleMeshRigidBodyShape
import io.github.movementspeed.nhglib.physics.models.ConvexHullRigidBodyShape
import io.github.movementspeed.nhglib.physics.models.ConvexTriangleMeshRigidBodyShape
import io.github.movementspeed.nhglib.physics.models.RigidBodyShape
import io.github.movementspeed.nhglib.utils.data.Bundle
import io.github.movementspeed.nhglib.utils.data.Strings
import io.github.movementspeed.nhglib.utils.scenes.SceneMappings
import io.reactivex.functions.Consumer

/**
 * Created by Fausto Napoli on 08/12/2016.
 */
class SceneManager(private val messaging: Messaging, private val entities: Entities, private val assets: Assets) {
    var currentScene: Scene? = null
        private set

    private val nodeMapper: ComponentMapper<NodeComponent>
    private val modelMapper: ComponentMapper<ModelComponent>
    private val rigidBodyMapper: ComponentMapper<RigidBodyComponent>
    private val vehicleMapper: ComponentMapper<VehicleComponent>
    private val vehicleWheelMapper: ComponentMapper<TyreComponent>
    private val particleEffectMapper: ComponentMapper<ParticleEffectComponent>

    init {

        nodeMapper = entities.getMapper(NodeComponent::class.java)
        modelMapper = entities.getMapper(ModelComponent::class.java)
        rigidBodyMapper = entities.getMapper(RigidBodyComponent::class.java)
        vehicleMapper = entities.getMapper(VehicleComponent::class.java)
        vehicleWheelMapper = entities.getMapper(TyreComponent::class.java)
        particleEffectMapper = entities.getMapper(ParticleEffectComponent::class.java)

        SceneMappings.addComponentJsonMapping("message", MessageComponentJson::class.java)
        SceneMappings.addComponentJsonMapping("camera", CameraComponentJson::class.java)
        SceneMappings.addComponentJsonMapping("light", LightComponentJson::class.java)
        SceneMappings.addComponentJsonMapping("model", ModelComponentJson::class.java)
        SceneMappings.addComponentJsonMapping("rigidBody", RigidBodyComponentJson::class.java)
        SceneMappings.addComponentJsonMapping("vehicle", VehicleComponentJson::class.java)
        SceneMappings.addComponentJsonMapping("wheel", TyreComponentJson::class.java)
        SceneMappings.addComponentJsonMapping("particleEffect", ParticleEffectComponentJson::class.java)
        SceneMappings.addComponentJsonMapping("ui", UiComponentJson::class.java)

        SceneMappings.addAssetClassMapping("model", Model::class.java)
        SceneMappings.addAssetClassMapping("texture", Texture::class.java)
        SceneMappings.addAssetClassMapping("particleEffect", ParticleEffect::class.java)
        SceneMappings.addAssetClassMapping("hdr", HDRData::class.java)
        SceneMappings.addAssetClassMapping("textureAtlas", TextureAtlas::class.java)
        SceneMappings.addAssetClassMapping("bitmapFont", BitmapFont::class.java)
    }

    fun loadScene(scene: Scene) {
        currentScene = scene
        processAssets(scene.assets)

        val assetPackage = AssetPackage(scene.name)
        assetPackage.addAssets(scene.assets)
        assets.queueAssetPackage(assetPackage)

        messaging.get(Strings.Events.assetPackageLoaded)
                .subscribe { message ->
                    val assetPackage = message.data!![Strings.Defaults.assetPackageKey] as AssetPackage

                    if (assetPackage.`is`(scene.name)) {
                        for (entity in scene.sceneGraph.entities) {
                            processEntityAssets(entity, true)
                        }

                        val messageBundle = Bundle()
                        messageBundle[Strings.Defaults.sceneKey] = scene
                        messaging.send(Message(Strings.Events.sceneLoaded, messageBundle))
                    }
                }
    }

    fun unloadScene(scene: Scene) {
        for (entity in scene.sceneGraph.entities) {
            processEntityAssets(entity, false)
        }
    }

    fun reloadScene() {
        loadScene(currentScene!!)
    }

    private fun processAssets(assets: Array<Asset>) {
        for (asset in assets) {
            if (asset.isType(ParticleEffect::class.java)) {
                val particleRenderingSystem = entities.getEntitySystem(ParticleRenderingSystem::class.java)
                asset.parameters = ParticleEffectLoader.ParticleEffectLoadParameter(particleRenderingSystem.particleSystem.batches)
            }
        }
    }

    private fun processEntityAssets(entity: Int, load: Boolean) {
        // Check if this entity has a model
        if (modelMapper.has(entity)) {
            processModelComponent(entity, load)
        }

        // Check if this entity has a rigid body
        if (rigidBodyMapper.has(entity)) {
            processRigidBodyComponent(entity, load)
        }

        // Check if this entity is a vehicle
        if (vehicleMapper.has(entity)) {
            processVehicleComponent(entity, load)
        }

        // Check if this entity is a wheel
        if (vehicleWheelMapper.has(entity)) {
            processWheelComponent(entity)
        }

        // Check if this entity has a particle effect
        if (particleEffectMapper.has(entity)) {
            processParticleEffectComponent(entity, load)
        }
    }

    private fun processModelComponent(entity: Int, load: Boolean) {
        val modelComponent = modelMapper.get(entity)
        val nodeComponent = nodeMapper.get(entity)

        if (load) {
            val model = assets.get<Model>(modelComponent.asset)
            modelComponent.buildWithModel(nodeComponent.node.scale, model)

            for (pbrMaterial in modelComponent.pbrMaterials) {
                val mag = Texture.TextureFilter.Linear
                val min = Texture.TextureFilter.Linear
                val wrapU = Texture.TextureWrap.Repeat
                val wrapV = Texture.TextureWrap.Repeat

                if (pbrMaterial.albedo != null && !pbrMaterial.albedo!!.isEmpty()) {
                    val albedo = assets.get<Texture>(pbrMaterial.albedo)

                    if (albedo != null) {
                        albedo.setFilter(mag, min)
                        albedo.setWrap(wrapU, wrapV)
                        pbrMaterial.set(PBRTextureAttribute.createAlbedo(albedo,
                                pbrMaterial.offsetU, pbrMaterial.offsetV, pbrMaterial.tilesU, pbrMaterial.tilesV))
                    }
                } else if (pbrMaterial.albedoColor != null) {
                    pbrMaterial.set(PBRTextureAttribute.createAlbedo(pbrMaterial.albedoColor))
                } else {
                    if (pbrMaterial.targetNode != null && !pbrMaterial.targetNode!!.isEmpty()) {
                        val node = modelComponent.model.getNode(pbrMaterial.targetNode)

                        for (nodePart in node.parts) {
                            val attribute = nodePart.material.get(PBRTextureAttribute.Albedo) as PBRTextureAttribute

                            if (attribute != null) {
                                attribute.tilesU = pbrMaterial.tilesU
                                attribute.tilesV = pbrMaterial.tilesV
                                attribute.offsetU = pbrMaterial.offsetU
                                attribute.offsetV = pbrMaterial.offsetV
                            }
                        }
                    }
                }

                if (pbrMaterial.normal != null && !pbrMaterial.normal!!.isEmpty()) {
                    val normal = assets.get<Texture>(pbrMaterial.normal)

                    if (normal != null) {
                        normal.setFilter(mag, min)
                        normal.setWrap(wrapU, wrapV)
                        pbrMaterial.set(PBRTextureAttribute.createNormal(normal,
                                pbrMaterial.offsetU, pbrMaterial.offsetV, pbrMaterial.tilesU, pbrMaterial.tilesV))
                    }
                } else {
                    if (pbrMaterial.targetNode != null && !pbrMaterial.targetNode!!.isEmpty()) {
                        val node = modelComponent.model.getNode(pbrMaterial.targetNode)

                        for (nodePart in node.parts) {
                            val attribute = nodePart.material.get(PBRTextureAttribute.Normal) as PBRTextureAttribute

                            if (attribute != null) {
                                attribute.tilesU = pbrMaterial.tilesU
                                attribute.tilesV = pbrMaterial.tilesV
                                attribute.offsetU = pbrMaterial.offsetU
                                attribute.offsetV = pbrMaterial.offsetV
                            }
                        }
                    }
                }

                if (pbrMaterial.rma != null && !pbrMaterial.rma!!.isEmpty()) {
                    val rma = assets.get<Texture>(pbrMaterial.rma)

                    if (rma != null) {
                        rma.setFilter(mag, min)
                        rma.setWrap(wrapU, wrapV)
                        pbrMaterial.set(PBRTextureAttribute.createRMA(rma,
                                pbrMaterial.offsetU, pbrMaterial.offsetV, pbrMaterial.tilesU, pbrMaterial.tilesV))
                    }
                } else {
                    val roughness = MathUtils.clamp(pbrMaterial.roughnessValue, 0.01f, 1f)
                    val metalness = MathUtils.clamp(pbrMaterial.metalnessValue, 0.01f, 1f)
                    val ao = MathUtils.clamp(pbrMaterial.aoValue, 0f, 1f)

                    if (pbrMaterial.targetNode != null && !pbrMaterial.targetNode!!.isEmpty()) {
                        val node = modelComponent.model.getNode(pbrMaterial.targetNode)

                        for (nodePart in node.parts) {
                            val attribute = nodePart.material.get(PBRTextureAttribute.RMA) as PBRTextureAttribute

                            if (attribute != null) {
                                attribute.tilesU = pbrMaterial.tilesU
                                attribute.tilesV = pbrMaterial.tilesV
                                attribute.offsetU = pbrMaterial.offsetU
                                attribute.offsetV = pbrMaterial.offsetV
                            } else {
                                pbrMaterial.set(PBRTextureAttribute.createRMA(roughness, metalness, ao))
                            }
                        }
                    }
                }

                if (pbrMaterial.blended) {
                    pbrMaterial.set(BlendingAttribute(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA))
                }

                if (pbrMaterial.targetNode != null && !pbrMaterial.targetNode!!.isEmpty()) {
                    modelComponent.setPBRMaterial(pbrMaterial.targetNode, pbrMaterial)
                } else {
                    modelComponent.setPBRMaterial(pbrMaterial)
                }
            }
        } else {
            assets.unloadAsset(modelComponent.asset)

            for (mat in modelComponent.pbrMaterials) {
                assets.unloadAsset(mat.albedo)
                assets.unloadAsset(mat.normal)
                assets.unloadAsset(mat.rma)
            }
        }
    }

    private fun processRigidBodyComponent(entity: Int, load: Boolean) {
        val rigidBodyComponent = rigidBodyMapper.get(entity)

        if (load) {
            rigidBodyComponent.build(assets)
            rigidBodyComponent.state = RigidBodyComponent.State.READY
        } else {
            val rigidBodyShape = rigidBodyComponent.rigidBodyShape

            when (rigidBodyShape!!.type) {
                RigidBodyType.CONVEX_TRIANGLE_MESH -> {
                    val convexTriangleMeshRigidBodyShape = rigidBodyComponent.rigidBodyShape as ConvexTriangleMeshRigidBodyShape
                    assets.unloadAsset(convexTriangleMeshRigidBodyShape.asset)
                }

                RigidBodyType.BVH_TRIANGLE_MESH -> {
                    val bvhTriangleMeshRigidBodyShape = rigidBodyComponent.rigidBodyShape as BvhTriangleMeshRigidBodyShape
                    assets.unloadAsset(bvhTriangleMeshRigidBodyShape.asset)
                }

                RigidBodyType.CONVEX_HULL -> {
                    val convexHullRigidBodyShape = rigidBodyComponent.rigidBodyShape as ConvexHullRigidBodyShape
                    assets.unloadAsset(convexHullRigidBodyShape.asset)
                }
            }
        }
    }

    private fun processVehicleComponent(entity: Int, load: Boolean) {
        val vehicleComponent = vehicleMapper.get(entity)

        if (load) {
            val physicsSystem = entities.getEntitySystem(PhysicsSystem::class.java)
            vehicleComponent.build(assets, physicsSystem.bulletWorld)
            vehicleComponent.state = RigidBodyComponent.State.READY
        } else {
            val rigidBodyShape = vehicleComponent.rigidBodyShape

            when (rigidBodyShape!!.type) {
                RigidBodyType.CONVEX_TRIANGLE_MESH -> {
                    val convexTriangleMeshRigidBodyShape = vehicleComponent.rigidBodyShape as ConvexTriangleMeshRigidBodyShape
                    assets.unloadAsset(convexTriangleMeshRigidBodyShape.asset)
                }

                RigidBodyType.BVH_TRIANGLE_MESH -> {
                    val bvhTriangleMeshRigidBodyShape = vehicleComponent.rigidBodyShape as BvhTriangleMeshRigidBodyShape
                    assets.unloadAsset(bvhTriangleMeshRigidBodyShape.asset)
                }

                RigidBodyType.CONVEX_HULL -> {
                    val convexHullRigidBodyShape = vehicleComponent.rigidBodyShape as ConvexHullRigidBodyShape
                    assets.unloadAsset(convexHullRigidBodyShape.asset)
                }
            }
        }
    }

    private fun processWheelComponent(entity: Int) {
        val tyreComponent = vehicleWheelMapper.get(entity)
        tyreComponent.build()
        tyreComponent.state = TyreComponent.State.READY
    }

    private fun processParticleEffectComponent(entity: Int, load: Boolean) {
        val particleEffectComponent = particleEffectMapper.get(entity)

        if (load) {
            val particleRenderingSystem = entities.getEntitySystem(ParticleRenderingSystem::class.java)
            particleEffectComponent.build(assets, particleRenderingSystem.particleEffectProvider)
            particleEffectComponent.state = ParticleEffectComponent.State.READY
        } else {
            assets.unloadAsset(particleEffectComponent.asset)
        }
    }
}
