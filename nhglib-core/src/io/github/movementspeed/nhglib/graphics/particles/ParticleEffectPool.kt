package io.github.movementspeed.nhglib.graphics.particles

import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect
import com.badlogic.gdx.utils.Pool

class ParticleEffectPool(private val originalEffect: ParticleEffect) : Pool<ParticleEffect>() {

    override fun free(`object`: ParticleEffect) {
        `object`.reset()
        super.free(`object`)
    }

    override fun newObject(): ParticleEffect {
        return originalEffect.copy()
    }
}
