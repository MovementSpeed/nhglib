package io.github.movementspeed.nhglib.data.models.serialization

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.JsonValue
import io.github.movementspeed.nhglib.graphics.utils.PBRMaterial
import io.github.movementspeed.nhglib.interfaces.JsonParseable

/**
 * Created by Fausto Napoli on 28/03/2017.
 */
class PBRMaterialJson : JsonParseable<PBRMaterial> {
    private var pbrMaterial: PBRMaterial? = null

    override fun parse(jsonValue: JsonValue) {
        pbrMaterial = PBRMaterial()
        pbrMaterial!!.targetNode = jsonValue.getString("targetNode", "")

        val blended = jsonValue.getBoolean("blended", false)

        val albedo = jsonValue.getString("albedo", "")
        val normal = jsonValue.getString("normal", "")
        val rma = jsonValue.getString("rma", "")

        pbrMaterial!!.blended = blended

        if (jsonValue.has("metalnessValue")) {
            pbrMaterial!!.metalnessValue = jsonValue.getFloat("metalnessValue", 0.01f)
        } else {
            pbrMaterial!!.metalnessValue = 0.01f
        }

        if (jsonValue.has("roughnessValue")) {
            pbrMaterial!!.roughnessValue = jsonValue.getFloat("roughnessValue", 0.8f)
        } else {
            pbrMaterial!!.roughnessValue = 0.8f
        }

        if (jsonValue.has("aoValue")) {
            pbrMaterial!!.aoValue = jsonValue.getFloat("aoValue", 1.0f)
        } else {
            pbrMaterial!!.aoValue = 1.0f
        }

        pbrMaterial!!.offsetU = jsonValue.getFloat("offsetU", 0f)
        pbrMaterial!!.offsetV = jsonValue.getFloat("offsetV", 0f)
        pbrMaterial!!.tilesU = jsonValue.getFloat("tilesU", 1f)
        pbrMaterial!!.tilesV = jsonValue.getFloat("tilesV", 1f)

        val albedoColorJson = jsonValue.get("albedoColor")

        if (albedoColorJson != null) {
            pbrMaterial!!.albedoColor = Color(
                    albedoColorJson.getFloat("r", 1.0f),
                    albedoColorJson.getFloat("g", 1.0f),
                    albedoColorJson.getFloat("b", 1.0f),
                    albedoColorJson.getFloat("a", 1.0f))
        }

        if (albedo != null) {
            pbrMaterial!!.albedo = albedo
        }

        if (rma != null) {
            pbrMaterial!!.rma = rma
        }

        if (normal != null) {
            pbrMaterial!!.normal = normal
        }
    }

    override fun get(): PBRMaterial? {
        return pbrMaterial
    }
}
