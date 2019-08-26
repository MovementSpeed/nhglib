package io.github.movementspeed.nhglib.graphics.particles

import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect
import com.badlogic.gdx.utils.ArrayMap

class ParticleEffectProvider {
    private val particleEffectPoolMap: ArrayMap<String, ParticleEffectPool>

    init {
        particleEffectPoolMap = ArrayMap()
    }

    fun addParticleEffect(asset: String, effect: ParticleEffect) {
        if (!particleEffectPoolMap.containsKey(asset)) {
            val particleEffectPool = ParticleEffectPool(effect)
            particleEffectPoolMap.put(asset, particleEffectPool)
        }
    }

    fun obtainParticleEffect(asset: String): ParticleEffect {
        return particleEffectPoolMap.get(asset).obtain()
    }
}
