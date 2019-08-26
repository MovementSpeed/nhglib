package io.github.movementspeed.nhglib.core.ecs.systems.impl

import com.artemis.Aspect
import com.artemis.ComponentMapper
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem
import io.github.movementspeed.nhglib.core.ecs.components.graphics.ParticleEffectComponent
import io.github.movementspeed.nhglib.core.ecs.components.scenes.NodeComponent
import io.github.movementspeed.nhglib.core.ecs.systems.base.BaseRenderingSystem
import io.github.movementspeed.nhglib.core.ecs.utils.Entities
import io.github.movementspeed.nhglib.graphics.particles.ParticleEffectProvider
import io.github.movementspeed.nhglib.graphics.particles.PointSpriteSoftParticleBatch
import io.github.movementspeed.nhglib.graphics.shaders.particles.ParticleShader

class ParticleRenderingSystem(entities: Entities) : BaseRenderingSystem(Aspect.all(NodeComponent::class.java, ParticleEffectComponent::class.java), entities) {
    private var initialized: Boolean = false

    val particleSystem: ParticleSystem
    private var pointSpriteBatch: PointSpriteSoftParticleBatch? = null
    val particleEffectProvider: ParticleEffectProvider

    private val nodeMapper: ComponentMapper<NodeComponent>? = null
    private val particleEffectMapper: ComponentMapper<ParticleEffectComponent>? = null

    init {
        particleSystem = ParticleSystem()
        particleEffectProvider = ParticleEffectProvider()
    }

    override fun begin() {
        super.begin()

        if (!initialized) {
            initialized = true

            val folder = "shaders/"

            val config = ParticleShader.Config(
                    Gdx.files.internal(folder + "particle_shader.vert").readString(),
                    Gdx.files.internal(folder + "particle_shader.frag").readString())

            config.type = ParticleShader.ParticleType.Point
            config.align = ParticleShader.AlignMode.Screen

            pointSpriteBatch = PointSpriteSoftParticleBatch(renderingSystem, 1000, config)
            particleSystem.add(pointSpriteBatch)
        }
    }

    override fun process(entityId: Int) {
        val nodeComponent = nodeMapper!!.get(entityId)
        val particleEffectComponent = particleEffectMapper!!.get(entityId)
        renderableProviders.add(particleSystem)

        if (particleEffectComponent.state == ParticleEffectComponent.State.READY) {
            val particleEffect = particleEffectComponent.particleEffect

            if (!particleEffectComponent.added) {
                particleSystem.add(particleEffect)
                particleEffectComponent.added = true
            }

            particleEffect.setTransform(nodeComponent.transform)
        }
    }

    override fun end() {
        super.end()

        for (i in 0 until cameras!!.size) {
            val camera = cameras!!.get(i)

            pointSpriteBatch!!.setCamera(camera)
            particleSystem.update()
            particleSystem.begin()
            particleSystem.draw()
            particleSystem.end()
        }
    }
}
