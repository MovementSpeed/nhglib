package io.github.movementspeed.nhglib.assets.loaders

import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.VertexAttribute
import com.badlogic.gdx.graphics.g3d.model.data.*
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.*
import io.github.movementspeed.nhglib.assets.Asset
import io.github.movementspeed.nhglib.assets.Assets

class NhgG3dModelLoader @JvmOverloads constructor(private val assets: Assets, protected val reader: BaseJsonReader, resolver: FileHandleResolver? = null) : NhgModelLoader<NhgModelLoader.ModelParameters>(resolver) {

    private val tempQ = Quaternion()

    override fun loadModelData(fileHandle: FileHandle, parameters: NhgModelLoader.ModelParameters?): ModelData? {
        return parseModel(fileHandle)
    }

    fun parseModel(handle: FileHandle): ModelData {
        val json = reader.parse(handle)
        val model = ModelData()
        val version = json.require("version")
        model.version[0] = version.getShort(0)
        model.version[1] = version.getShort(1)
        if (model.version[0] != VERSION_HI || model.version[1] != VERSION_LO)
            throw GdxRuntimeException("Model version not supported")

        // Get the current asset
        val cachedAssets = assets.cachedAssets
        for (asset in cachedAssets) {
            if (asset.source.contentEquals(handle.path())) {
                currentAsset = asset
                break
            }
        }

        model.id = json.getString("id", "")
        parseMeshes(model, json)
        parseMaterials(model, json, currentAsset.dependenciesPath)
        parseNodes(model, json)
        parseAnimations(model, json)
        return model
    }

    private fun parseMeshes(model: ModelData, json: JsonValue) {
        val meshes = json.get("meshes")
        if (meshes != null) {

            model.meshes.ensureCapacity(meshes.size)
            var mesh: JsonValue? = meshes.child
            while (mesh != null) {
                val jsonMesh = ModelMesh()

                val id = mesh.getString("id", "")
                jsonMesh.id = id

                val attributes = mesh.require("attributes")
                jsonMesh.attributes = parseAttributes(attributes)
                jsonMesh.vertices = mesh.require("vertices").asFloatArray()

                val meshParts = mesh.require("parts")
                val parts = Array<ModelMeshPart>()
                var meshPart: JsonValue? = meshParts.child
                while (meshPart != null) {
                    val jsonPart = ModelMeshPart()
                    val partId = meshPart.getString("id", null)
                            ?: throw GdxRuntimeException("Not id given for mesh part")
                    for (other in parts) {
                        if (other.id == partId) {
                            throw GdxRuntimeException("Mesh part with id '$partId' already in defined")
                        }
                    }
                    jsonPart.id = partId

                    val type = meshPart.getString("type", null)
                            ?: throw GdxRuntimeException("No primitive type given for mesh part '$partId'")
                    jsonPart.primitiveType = parseType(type)

                    jsonPart.indices = meshPart.require("indices").asShortArray()
                    parts.add(jsonPart)
                    meshPart = meshPart.next
                }
                jsonMesh.parts = parts.toArray(ModelMeshPart::class.java)
                model.meshes.add(jsonMesh)
                mesh = mesh.next
            }
        }
    }

    private fun parseType(type: String): Int {
        return if (type == "TRIANGLES") {
            GL20.GL_TRIANGLES
        } else if (type == "LINES") {
            GL20.GL_LINES
        } else if (type == "POINTS") {
            GL20.GL_POINTS
        } else if (type == "TRIANGLE_STRIP") {
            GL20.GL_TRIANGLE_STRIP
        } else if (type == "LINE_STRIP") {
            GL20.GL_LINE_STRIP
        } else {
            throw GdxRuntimeException("Unknown primitive type '" + type
                    + "', should be one of triangle, trianglestrip, line, linestrip, lineloop or point")
        }
    }

    private fun parseAttributes(attributes: JsonValue): Array<VertexAttribute> {
        val vertexAttributes = Array<VertexAttribute>()
        var unit = 0
        var blendWeightCount = 0
        var value: JsonValue? = attributes.child
        while (value != null) {
            val attribute = value.asString()
            val attr = attribute as String
            if (attr == "POSITION") {
                vertexAttributes.add(VertexAttribute.Position())
            } else if (attr == "NORMAL") {
                vertexAttributes.add(VertexAttribute.Normal())
            } else if (attr == "COLOR") {
                vertexAttributes.add(VertexAttribute.ColorUnpacked())
            } else if (attr == "COLORPACKED") {
                vertexAttributes.add(VertexAttribute.ColorPacked())
            } else if (attr == "TANGENT") {
                vertexAttributes.add(VertexAttribute.Tangent())
            } else if (attr == "BINORMAL") {
                vertexAttributes.add(VertexAttribute.Binormal())
            } else if (attr.startsWith("TEXCOORD")) {
                vertexAttributes.add(VertexAttribute.TexCoords(unit++))
            } else if (attr.startsWith("BLENDWEIGHT")) {
                vertexAttributes.add(VertexAttribute.BoneWeight(blendWeightCount++))
            } else {
                throw GdxRuntimeException("Unknown vertex attribute '" + attr
                        + "', should be one of position, normal, uv, tangent or binormal")
            }
            value = value.next
        }
        return vertexAttributes.toArray(VertexAttribute::class.java)
    }

    private fun parseMaterials(model: ModelData, json: JsonValue, materialDir: String) {
        val materials = json.get("materials")

        if (materials == null) {
            // we should probably create some default material in this case
        } else {
            model.materials.ensureCapacity(materials.size)
            var material: JsonValue? = materials.child
            while (material != null) {
                val jsonMaterial = ModelMaterial()

                val id = material.getString("id", null) ?: throw GdxRuntimeException("Material needs an id.")

                jsonMaterial.id = id

                // Read material colors
                val albedo = material.get("diffuse")
                if (albedo != null) jsonMaterial.diffuse = parseColor(albedo)

                val ambient = material.get("ambient")
                if (ambient != null) jsonMaterial.ambient = parseColor(ambient)

                // Read opacity
                jsonMaterial.opacity = 1.0f/*material.getFloat("opacity", 1.0f)*/

                // Read textures
                val textures = material.get("textures")
                if (textures != null) {
                    var texture: JsonValue? = textures.child
                    while (texture != null) {
                        val jsonTexture = ModelTexture()

                        val textureId = texture.getString("id", null) ?: throw GdxRuntimeException("Texture has no id.")
                        jsonTexture.id = textureId

                        val fileName = texture.getString("filename", null)
                                ?: throw GdxRuntimeException("Texture needs filename.")
                        jsonTexture.fileName = (materialDir + (if (materialDir.length == 0 || materialDir.endsWith("/")) "" else "/")
                                + fileName)

                        jsonTexture.uvTranslation = readVector2(texture.get("uvTranslation"), 0f, 0f)
                        jsonTexture.uvScaling = readVector2(texture.get("uvScaling"), 1f, 1f)

                        val textureType = texture.getString("type", null)
                                ?: throw GdxRuntimeException("Texture needs type.")

                        jsonTexture.usage = parseTextureUsage(textureType)

                        if (jsonMaterial.textures == null) jsonMaterial.textures = Array()
                        jsonMaterial.textures.add(jsonTexture)
                        texture = texture.next
                    }
                }

                model.materials.add(jsonMaterial)
                material = material.next
            }
        }
    }

    private fun parseTextureUsage(value: String): Int {
        if (value.equals("DIFFUSE", ignoreCase = true))
            return ModelTexture.USAGE_DIFFUSE
        else if (value.equals("EMISSIVE", ignoreCase = true))
            return ModelTexture.USAGE_EMISSIVE
        else if (value.equals("NONE", ignoreCase = true))
            return ModelTexture.USAGE_NONE
        else if (value.equals("BUMP", ignoreCase = true))
            return ModelTexture.USAGE_NORMAL
        else if (value.equals("SPECULAR", ignoreCase = true))
            return ModelTexture.USAGE_SPECULAR
        else if (value.equals("TRANSPARENCY", ignoreCase = true))
            return ModelTexture.USAGE_TRANSPARENCY

        return ModelTexture.USAGE_UNKNOWN
    }

    private fun parseColor(colorArray: JsonValue): Color {
        return if (colorArray.size >= 3)
            Color(colorArray.getFloat(0), colorArray.getFloat(1), colorArray.getFloat(2), 1.0f)
        else
            throw GdxRuntimeException("Expected Color values <> than three.")
    }

    private fun readVector2(vectorArray: JsonValue?, x: Float, y: Float): Vector2 {
        return if (vectorArray == null)
            Vector2(x, y)
        else if (vectorArray.size == 2)
            Vector2(vectorArray.getFloat(0), vectorArray.getFloat(1))
        else
            throw GdxRuntimeException("Expected Vector2 values <> than two.")
    }

    private fun parseNodes(model: ModelData, json: JsonValue): Array<ModelNode> {
        val nodes = json.get("nodes")
        if (nodes != null) {
            model.nodes.ensureCapacity(nodes.size)
            var node: JsonValue? = nodes.child
            while (node != null) {
                model.nodes.add(parseNodesRecursively(node))
                node = node.next
            }
        }

        return model.nodes
    }

    private fun parseNodesRecursively(json: JsonValue): ModelNode {
        val jsonNode = ModelNode()

        val id = json.getString("id", null) ?: throw GdxRuntimeException("Node id missing.")
        jsonNode.id = id

        val translation = json.get("translation")
        if (translation != null && translation.size != 3) throw GdxRuntimeException("Node translation incomplete")
        jsonNode.translation = if (translation == null)
            null
        else
            Vector3(translation.getFloat(0), translation.getFloat(1),
                    translation.getFloat(2))

        val rotation = json.get("rotation")
        if (rotation != null && rotation.size != 4) throw GdxRuntimeException("Node rotation incomplete")
        jsonNode.rotation = if (rotation == null)
            null
        else
            Quaternion(rotation.getFloat(0), rotation.getFloat(1),
                    rotation.getFloat(2), rotation.getFloat(3))

        val scale = json.get("scale")
        if (scale != null && scale.size != 3) throw GdxRuntimeException("Node scale incomplete")
        jsonNode.scale = if (scale == null) null else Vector3(scale.getFloat(0), scale.getFloat(1), scale.getFloat(2))

        val meshId = json.getString("mesh", null)
        if (meshId != null) jsonNode.meshId = meshId

        val materials = json.get("parts")
        if (materials != null) {
            jsonNode.parts = arrayOfNulls(materials.size)
            var i = 0
            var material: JsonValue? = materials.child
            while (material != null) {
                val nodePart = ModelNodePart()

                val meshPartId = material.getString("meshpartid", null)
                val materialId = material.getString("materialid", null)
                if (meshPartId == null || materialId == null) {
                    throw GdxRuntimeException("Node $id part is missing meshPartId or materialId")
                }
                nodePart.materialId = materialId
                nodePart.meshPartId = meshPartId

                val bones = material.get("bones")
                if (bones != null) {
                    nodePart.bones = ArrayMap(true, bones.size, String::class.java, Matrix4::class.java)
                    var j = 0
                    var bone: JsonValue? = bones.child
                    while (bone != null) {
                        val nodeId = bone.getString("node", null) ?: throw GdxRuntimeException("Bone node ID missing")

                        val transform = Matrix4()

                        var `val`: JsonValue? = bone.get("translation")
                        if (`val` != null && `val`.size >= 3)
                            transform.translate(`val`.getFloat(0), `val`.getFloat(1), `val`.getFloat(2))

                        `val` = bone.get("rotation")
                        if (`val` != null && `val`.size >= 4)
                            transform.rotate(tempQ.set(`val`.getFloat(0), `val`.getFloat(1), `val`.getFloat(2), `val`.getFloat(3)))

                        `val` = bone.get("scale")
                        if (`val` != null && `val`.size >= 3)
                            transform.scale(`val`.getFloat(0), `val`.getFloat(1), `val`.getFloat(2))

                        nodePart.bones.put(nodeId, transform)
                        bone = bone.next
                        j++
                    }
                }

                jsonNode.parts[i] = nodePart
                material = material.next
                i++
            }
        }

        val children = json.get("children")
        if (children != null) {
            jsonNode.children = arrayOfNulls(children.size)

            var i = 0
            var child: JsonValue? = children.child
            while (child != null) {
                jsonNode.children[i] = parseNodesRecursively(child)
                child = child.next
                i++
            }
        }

        return jsonNode
    }

    private fun parseAnimations(model: ModelData, json: JsonValue) {
        val animations = json.get("animations") ?: return

        model.animations.ensureCapacity(animations.size)

        var anim: JsonValue? = animations.child
        while (anim != null) {
            val nodes = anim.get("bones")
            if (nodes == null) {
                anim = anim.next
                continue
            }
            val animation = ModelAnimation()
            model.animations.add(animation)
            animation.nodeAnimations.ensureCapacity(nodes.size)
            animation.id = anim.getString("id")
            var node: JsonValue? = nodes.child
            while (node != null) {
                val nodeAnim = ModelNodeAnimation()
                animation.nodeAnimations.add(nodeAnim)
                nodeAnim.nodeId = node.getString("boneId")

                // For backwards compatibility (version 0.1):
                val keyframes = node.get("keyframes")
                if (keyframes != null && keyframes.isArray) {
                    var keyframe: JsonValue? = keyframes.child
                    while (keyframe != null) {
                        val keytime = keyframe.getFloat("keytime", 0f) / 1000f
                        val translation = keyframe.get("translation")
                        if (translation != null && translation.size == 3) {
                            if (nodeAnim.translation == null)
                                nodeAnim.translation = Array()
                            val tkf = ModelNodeKeyframe<Vector3>()
                            tkf.keytime = keytime
                            tkf.value = Vector3(translation.getFloat(0), translation.getFloat(1), translation.getFloat(2))
                            nodeAnim.translation.add(tkf)
                        }
                        val rotation = keyframe.get("rotation")
                        if (rotation != null && rotation.size == 4) {
                            if (nodeAnim.rotation == null)
                                nodeAnim.rotation = Array()
                            val rkf = ModelNodeKeyframe<Quaternion>()
                            rkf.keytime = keytime
                            rkf.value = Quaternion(rotation.getFloat(0), rotation.getFloat(1), rotation.getFloat(2), rotation.getFloat(3))
                            nodeAnim.rotation.add(rkf)
                        }
                        val scale = keyframe.get("scale")
                        if (scale != null && scale.size == 3) {
                            if (nodeAnim.scaling == null)
                                nodeAnim.scaling = Array()
                            val skf = ModelNodeKeyframe()
                            skf.keytime = keytime
                            skf.value = Vector3(scale.getFloat(0), scale.getFloat(1), scale.getFloat(2))
                            nodeAnim.scaling.add(skf)
                        }
                        keyframe = keyframe.next
                    }
                } else { // Version 0.2:
                    val translationKF = node.get("translation")
                    if (translationKF != null && translationKF.isArray) {
                        nodeAnim.translation = Array()
                        nodeAnim.translation.ensureCapacity(translationKF.size)
                        var keyframe: JsonValue? = translationKF.child
                        while (keyframe != null) {
                            val kf = ModelNodeKeyframe<Vector3>()
                            nodeAnim.translation.add(kf)
                            kf.keytime = keyframe.getFloat("keytime", 0f) / 1000f
                            val translation = keyframe.get("value")
                            if (translation != null && translation.size >= 3)
                                kf.value = Vector3(translation.getFloat(0), translation.getFloat(1), translation.getFloat(2))
                            keyframe = keyframe.next
                        }
                    }


                    val rotationKF = node.get("rotation")
                    if (rotationKF != null && rotationKF.isArray) {
                        nodeAnim.rotation = Array()
                        nodeAnim.rotation.ensureCapacity(rotationKF.size)
                        var keyframe: JsonValue? = rotationKF.child
                        while (keyframe != null) {
                            val kf = ModelNodeKeyframe<Quaternion>()
                            nodeAnim.rotation.add(kf)
                            kf.keytime = keyframe.getFloat("keytime", 0f) / 1000f
                            val rotation = keyframe.get("value")
                            if (rotation != null && rotation.size >= 4)
                                kf.value = Quaternion(rotation.getFloat(0), rotation.getFloat(1), rotation.getFloat(2), rotation.getFloat(3))
                            keyframe = keyframe.next
                        }
                    }

                    val scalingKF = node.get("scaling")
                    if (scalingKF != null && scalingKF.isArray) {
                        nodeAnim.scaling = Array()
                        nodeAnim.scaling.ensureCapacity(scalingKF.size)
                        var keyframe: JsonValue? = scalingKF.child
                        while (keyframe != null) {
                            val kf = ModelNodeKeyframe<Vector3>()
                            nodeAnim.scaling.add(kf)
                            kf.keytime = keyframe.getFloat("keytime", 0f) / 1000f
                            val scaling = keyframe.get("value")
                            if (scaling != null && scaling.size >= 3)
                                kf.value = Vector3(scaling.getFloat(0), scaling.getFloat(1), scaling.getFloat(2))
                            keyframe = keyframe.next
                        }
                    }
                }
                node = node.next
            }
            anim = anim.next
        }
    }

    companion object {
        val VERSION_HI: Short = 0
        val VERSION_LO: Short = 1
    }
}