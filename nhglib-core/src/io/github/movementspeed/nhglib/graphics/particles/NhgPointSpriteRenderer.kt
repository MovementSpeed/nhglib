package io.github.movementspeed.nhglib.graphics.particles

import com.badlogic.gdx.graphics.g3d.particles.ParticleChannels
import com.badlogic.gdx.graphics.g3d.particles.ParticleChannels.ColorInitializer
import com.badlogic.gdx.graphics.g3d.particles.ParticleChannels.Rotation2dInitializer
import com.badlogic.gdx.graphics.g3d.particles.ParticleChannels.ScaleInitializer
import com.badlogic.gdx.graphics.g3d.particles.ParticleChannels.TextureRegionInitializer
import com.badlogic.gdx.graphics.g3d.particles.ParticleControllerComponent
import com.badlogic.gdx.graphics.g3d.particles.batches.ParticleBatch
import com.badlogic.gdx.graphics.g3d.particles.batches.PointSpriteParticleBatch
import com.badlogic.gdx.graphics.g3d.particles.renderers.ParticleControllerRenderer
import com.badlogic.gdx.graphics.g3d.particles.renderers.PointSpriteControllerRenderData

/**
 * A [ParticleControllerRenderer] which will render particles as point sprites to a [PointSpriteParticleBatch] .
 *
 * @author Inferno
 */
class NhgPointSpriteRenderer() : ParticleControllerRenderer<PointSpriteControllerRenderData, PointSpriteSoftParticleBatch>(PointSpriteControllerRenderData()) {

    constructor(batch: PointSpriteSoftParticleBatch) : this() {
        setBatch(batch)
    }

    override fun allocateChannels() {
        renderData.positionChannel = controller.particles.addChannel<ParallelArray.FloatChannel>(ParticleChannels.Position)
        renderData.regionChannel = controller.particles.addChannel<FloatChannel>(ParticleChannels.TextureRegion, TextureRegionInitializer.get())
        renderData.colorChannel = controller.particles.addChannel<FloatChannel>(ParticleChannels.Color, ColorInitializer.get())
        renderData.scaleChannel = controller.particles.addChannel<FloatChannel>(ParticleChannels.Scale, ScaleInitializer.get())
        renderData.rotationChannel = controller.particles.addChannel<FloatChannel>(ParticleChannels.Rotation2D, Rotation2dInitializer.get())
    }

    override fun isCompatible(batch: ParticleBatch<*>): Boolean {
        return batch is PointSpriteSoftParticleBatch
    }

    override fun copy(): ParticleControllerComponent {
        return NhgPointSpriteRenderer(batch)
    }

}