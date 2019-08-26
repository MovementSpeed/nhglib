package io.github.movementspeed.nhglib.enums

/**
 * Created by Fausto Napoli on 14/03/2017.
 */
enum class LightType {
    DIRECTIONAL_LIGHT,
    POINT_LIGHT,
    SPOT_LIGHT;


    companion object {

        fun fromString(value: String): LightType? {
            var res: LightType? = null

            when (value) {
                "directionalLight" -> res = DIRECTIONAL_LIGHT

                "pointLight" -> res = POINT_LIGHT

                "spotLight" -> res = SPOT_LIGHT
            }

            return res
        }
    }
}
