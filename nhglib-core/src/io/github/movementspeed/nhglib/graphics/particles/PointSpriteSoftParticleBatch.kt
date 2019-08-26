package io.github.movementspeed.nhglib.graphics.particles

/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.badlogic.gdx.Application.ApplicationType
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.VertexAttributes.Usage
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.particles.ParallelArray.FloatChannel
import com.badlogic.gdx.graphics.g3d.particles.ParticleChannels
import com.badlogic.gdx.graphics.g3d.particles.ResourceData
import com.badlogic.gdx.graphics.g3d.particles.ResourceData.SaveData
import com.badlogic.gdx.graphics.g3d.particles.batches.BufferedParticleBatch
import com.badlogic.gdx.graphics.g3d.particles.renderers.PointSpriteControllerRenderData
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Pool
import io.github.movementspeed.nhglib.core.ecs.systems.impl.RenderingSystem
import io.github.movementspeed.nhglib.graphics.shaders.particles.ParticleShader

/**
 * This class is used to draw particles as point sprites.
 *
 * @author Inferno
 */
class PointSpriteSoftParticleBatch @JvmOverloads constructor(renderingSystem: RenderingSystem, capacity: Int = 1000, shaderConfig: ParticleShader.Config = ParticleShader.Config(ParticleShader.ParticleType.Point)) : BufferedParticleBatch<PointSpriteControllerRenderData>(PointSpriteControllerRenderData::class.java) {

    private var vertices: FloatArray? = null
    internal var renderable: Renderable

    var texture: Texture
        get() {
            val attribute = renderable.material.get(TextureAttribute.Diffuse) as TextureAttribute
            return attribute.textureDescription.texture
        }
        set(texture) {
            val attribute = renderable.material.get(TextureAttribute.Diffuse) as TextureAttribute
            attribute.textureDescription.texture = texture
        }

    init {

        if (!pointSpritesEnabled) enablePointSprites()

        allocRenderable()
        ensureCapacity(capacity)

        val particleShader = ParticleShader(renderable, shaderConfig)
        particleShader.renderingSystem = renderingSystem
        particleShader.init()

        renderable.shader = particleShader
    }

    override fun allocParticlesData(capacity: Int) {
        vertices = FloatArray(capacity * CPU_VERTEX_SIZE)
        if (renderable.meshPart.mesh != null) renderable.meshPart.mesh.dispose()
        renderable.meshPart.mesh = Mesh(false, capacity, 0, CPU_ATTRIBUTES)
    }

    protected fun allocRenderable() {
        renderable = Renderable()
        renderable.meshPart.primitiveType = GL20.GL_POINTS
        renderable.meshPart.offset = 0
        renderable.material = Material(BlendingAttribute(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA, 1f),
                DepthTestAttribute(GL20.GL_LEQUAL, false), TextureAttribute.createDiffuse(null as Texture?))
    }

    override fun flush(offsets: IntArray) {
        var tp = 0
        for (data in renderData) {
            val scaleChannel = data.scaleChannel
            val regionChannel = data.regionChannel
            val positionChannel = data.positionChannel
            val colorChannel = data.colorChannel
            val rotationChannel = data.rotationChannel

            var p = 0
            while (p < data.controller.particles.size) {
                val offset = offsets[tp] * CPU_VERTEX_SIZE
                val regionOffset = p * regionChannel.strideSize
                val positionOffset = p * positionChannel.strideSize
                val colorOffset = p * colorChannel.strideSize
                val rotationOffset = p * rotationChannel.strideSize

                vertices[offset + CPU_POSITION_OFFSET] = positionChannel.data[positionOffset + ParticleChannels.XOffset]
                vertices[offset + CPU_POSITION_OFFSET + 1] = positionChannel.data[positionOffset + ParticleChannels.YOffset]
                vertices[offset + CPU_POSITION_OFFSET + 2] = positionChannel.data[positionOffset + ParticleChannels.ZOffset]
                vertices[offset + CPU_COLOR_OFFSET] = colorChannel.data[colorOffset + ParticleChannels.RedOffset]
                vertices[offset + CPU_COLOR_OFFSET + 1] = colorChannel.data[colorOffset + ParticleChannels.GreenOffset]
                vertices[offset + CPU_COLOR_OFFSET + 2] = colorChannel.data[colorOffset + ParticleChannels.BlueOffset]
                vertices[offset + CPU_COLOR_OFFSET + 3] = colorChannel.data[colorOffset + ParticleChannels.AlphaOffset]
                vertices[offset + CPU_SIZE_AND_ROTATION_OFFSET] = scaleChannel.data[p * scaleChannel.strideSize]
                vertices[offset + CPU_SIZE_AND_ROTATION_OFFSET + 1] = rotationChannel.data[rotationOffset + ParticleChannels.CosineOffset]
                vertices[offset + CPU_SIZE_AND_ROTATION_OFFSET + 2] = rotationChannel.data[rotationOffset + ParticleChannels.SineOffset]
                vertices[offset + CPU_REGION_OFFSET] = regionChannel.data[regionOffset + ParticleChannels.UOffset]
                vertices[offset + CPU_REGION_OFFSET + 1] = regionChannel.data[regionOffset + ParticleChannels.VOffset]
                vertices[offset + CPU_REGION_OFFSET + 2] = regionChannel.data[regionOffset + ParticleChannels.U2Offset]
                vertices[offset + CPU_REGION_OFFSET + 3] = regionChannel.data[regionOffset + ParticleChannels.V2Offset]
                ++p
                ++tp
            }
        }

        renderable.meshPart.size = bufferedParticlesCount
        renderable.meshPart.mesh.setVertices(vertices, 0, bufferedParticlesCount * CPU_VERTEX_SIZE)
        renderable.meshPart.update()
    }

    override fun getRenderables(renderables: Array<Renderable>, pool: Pool<Renderable>) {
        if (bufferedParticlesCount > 0) renderables.add(pool.obtain().set(renderable))
    }

    override fun save(manager: AssetManager, resources: ResourceData<*>) {
        val data = resources.createSaveData("pointSpriteBatch")
        data.saveAsset(manager.getAssetFileName(texture), Texture::class.java)
    }

    override fun load(manager: AssetManager, resources: ResourceData<*>) {
        val data = resources.getSaveData("pointSpriteBatch")
        if (data != null) texture = manager.get(data.loadAsset()) as Texture
    }

    companion object {
        private var pointSpritesEnabled = false
        protected val TMP_V1 = Vector3()
        protected val sizeAndRotationUsage = 1 shl 9
        protected val CPU_ATTRIBUTES = VertexAttributes(VertexAttribute(Usage.Position, 3,
                ShaderProgram.POSITION_ATTRIBUTE), VertexAttribute(Usage.ColorUnpacked, 4, ShaderProgram.COLOR_ATTRIBUTE),
                VertexAttribute(Usage.TextureCoordinates, 4, "a_region"), VertexAttribute(sizeAndRotationUsage, 3,
                "a_sizeAndRotation"))
        protected val CPU_VERTEX_SIZE = (CPU_ATTRIBUTES.vertexSize / 4).toShort().toInt()
        protected val CPU_POSITION_OFFSET = (CPU_ATTRIBUTES.findByUsage(Usage.Position).offset / 4).toShort().toInt()
        protected val CPU_COLOR_OFFSET = (CPU_ATTRIBUTES.findByUsage(Usage.ColorUnpacked).offset / 4).toShort().toInt()
        protected val CPU_REGION_OFFSET = (CPU_ATTRIBUTES.findByUsage(Usage.TextureCoordinates).offset / 4).toShort().toInt()
        protected val CPU_SIZE_AND_ROTATION_OFFSET = (CPU_ATTRIBUTES.findByUsage(sizeAndRotationUsage).offset / 4).toShort().toInt()

        private fun enablePointSprites() {
            Gdx.gl.glEnable(GL20.GL_VERTEX_PROGRAM_POINT_SIZE)
            if (Gdx.app.type == ApplicationType.Desktop) {
                Gdx.gl.glEnable(0x8861) // GL_POINT_OES
            }
            pointSpritesEnabled = true
        }
    }
}