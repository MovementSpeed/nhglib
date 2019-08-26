package io.github.movementspeed.nhglib.graphics.utils

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.Material

/**
 * Created by Fausto Napoli on 03/04/2017.
 */
class PBRMaterial : Material() {
    var blended: Boolean = false

    var metalnessValue: Float = 0f
    var roughnessValue: Float = 0f
    var aoValue: Float = 0f

    var offsetU: Float = 0f
    var offsetV: Float = 0f
    var tilesU: Float = 0f
    var tilesV: Float = 0f

    var targetNode: String? = null

    var albedo: String? = null
    var normal: String? = null
    var rma: String? = null

    var albedoColor: Color? = null
}
