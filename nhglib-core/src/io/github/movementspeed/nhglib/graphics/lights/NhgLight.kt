package io.github.movementspeed.nhglib.graphics.lights

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.environment.BaseLight
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import io.github.movementspeed.nhglib.enums.LightType

/**
 * Created by Fausto Napoli on 18/03/2017.
 */
class NhgLight : BaseLight<NhgLight>() {
    val position: Vector3
    val direction: Vector3

    var enabled: Boolean = false

    var radius: Float = 0.toFloat()
    var intensity: Float = 0.toFloat()
    var innerAngle: Float = 0.toFloat()
    var outerAngle: Float = 0.toFloat()

    var type: LightType

    private var transform: Matrix4? = null

    init {
        intensity = 0f
        enabled = true
        position = Vector3()
        direction = Vector3()
        transform = Matrix4()
    }

    fun set(light: NhgLight) {
        this.type = light.type
        this.enabled = light.enabled
        this.outerAngle = light.outerAngle
        this.innerAngle = light.innerAngle
        this.intensity = light.intensity
        this.radius = light.radius
        this.position.set(light.position)
        this.direction.set(light.direction)
        this.color.set(light.color)
    }

    fun setTransform(transform: Matrix4) {
        this.transform!!.set(transform)
    }

    fun copy(): NhgLight {
        val copy = NhgLight()

        copy.enabled = this.enabled
        copy.position.set(this.position)
        copy.direction.set(this.direction)
        copy.type = this.type
        copy.radius = this.radius
        copy.intensity = this.intensity
        copy.innerAngle = this.innerAngle
        copy.outerAngle = this.outerAngle
        copy.transform = this.transform

        return copy
    }

    fun getTransform(): Matrix4? {
        return transform
    }

    companion object {

        fun directional(intensity: Float, color: Color): NhgLight {
            val light = NhgLight()
            light.type = LightType.DIRECTIONAL_LIGHT
            light.radius = 1.0f
            light.intensity = intensity
            light.color.set(color)

            return light
        }

        fun point(intensity: Float, radius: Float, color: Color): NhgLight {
            val light = NhgLight()
            light.type = LightType.POINT_LIGHT
            light.intensity = intensity
            light.color.set(color)
            light.radius = radius

            return light
        }

        fun spot(intensity: Float, radius: Float, innerAngle: Float, outerAngle: Float, color: Color): NhgLight {
            val light = NhgLight()
            light.type = LightType.SPOT_LIGHT
            light.intensity = intensity
            light.radius = radius
            light.color.set(color)
            light.innerAngle = innerAngle
            light.outerAngle = outerAngle

            return light
        }
    }
}