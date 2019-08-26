package io.github.movementspeed.nhglib.core.ecs.components.graphics

import com.artemis.Component
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect
import io.github.movementspeed.nhglib.assets.Assets
import io.github.movementspeed.nhglib.graphics.particles.ParticleEffectProvider

class ParticleEffectComponent : Component() {
    var added: Boolean = false

    var asset: String? = null
    var state: State
    var particleEffect: ParticleEffect

    init {
        state = State.NOT_INITIALIZED
    }

    fun build(assets: Assets, particleEffectProvider: ParticleEffectProvider) {
        val particleEffect = assets.get<ParticleEffect>(asset)
        particleEffectProvider.addParticleEffect(asset, particleEffect)

        this.particleEffect = particleEffectProvider.obtainParticleEffect(asset)
        this.particleEffect.init()
    }

    enum class State {
        NOT_INITIALIZED,
        READY
    }
}
