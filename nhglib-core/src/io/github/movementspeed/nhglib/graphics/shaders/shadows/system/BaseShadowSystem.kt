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

package io.github.movementspeed.nhglib.graphics.shaders.shadows.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Cubemap.CubemapSide
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g3d.RenderableProvider
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.ObjectMap
import com.badlogic.gdx.utils.ObjectMap.Entries
import io.github.movementspeed.nhglib.enums.LightType
import io.github.movementspeed.nhglib.graphics.lights.NhgLight
import io.github.movementspeed.nhglib.graphics.shaders.shadows.utils.*

import java.util.EnumSet

/** BaseShadowSystem allows to easily create custom shadow system.
 * @author realitix
 */
abstract class BaseShadowSystem
/** Construct the system with the needed params.
 * @param nearFarAnalyzer Analyzer of near and far
 * @param allocator Allocator of shadow maps
 * @param directionalAnalyzer Analyze directional light to create orthographic camera
 * @param lightFilter Filter light to render
 */
@JvmOverloads constructor(
        /** Analyzer of near and far for spot and point lights  */
        protected var nearFarAnalyzer: NearFarAnalyzer = AABBNearFarAnalyzer(),
        /** Allocator which choose where to render shadow map in texture  */
        protected var allocator: ShadowMapAllocator = FixedShadowMapAllocator(FixedShadowMapAllocator.QUALITY_MED,
                FixedShadowMapAllocator.QUANTITY_MAP_MED),
        /** Analyzer which compute how to create the camera for directional light  */
        protected var directionalAnalyzer: DirectionalAnalyzer = BoundingSphereDirectionalAnalyzer(),
        /** Filter that choose if light must be rendered  */
        protected var lightFilter: LightFilter = FrustumLightFilter()) : ShadowSystem, Disposable {

    /** Main camera  */
    protected var camera: Camera? = null
    /** Renderable providers  */
    protected var renderableProviders: Iterable<RenderableProvider>? = null
    /** Cameras linked with spot lights  */
    var spotCameras = ObjectMap<NhgLight, LightProperties>()
        protected set
    /** Cameras linked with directional lights  */
    var directionalCameras = ObjectMap<NhgLight, LightProperties>()
        protected set
    /** Cameras linked with point lights  */
    var pointCameras = ObjectMap<NhgLight, PointLightProperties>()
        protected set
    /** Framebuffer used to render all the depth maps  */
    protected var frameBuffers: Array<FrameBuffer>? = null
    /** Current pass in the depth process  */
    var currentPass = -1
        protected set
    /** Iterators for cameras  */
    protected var spotCameraIterator: Entries<NhgLight, LightProperties>? = null
    protected var dirCameraIterator: Entries<NhgLight, LightProperties>? = null
    protected var pointCameraIterator: Entries<NhgLight, PointLightProperties>? = null
    /** Current side in the point light cubemap  */
    protected var currentPointSide: Int = 0
    protected var currentPointProperties: PointLightProperties? = null
    /** Shader providers used by this system  */
    protected var passShaderProviders: Array<ShaderProvider>? = null
    /** Current light and properties during shadowmap generation  */
    var currentLightProperties: LightProperties? = null
        protected set
    var currentLight: NhgLight? = null
        protected set

    /** getPassQuantity should return at leat one.  */
    abstract override val passQuantity: Int

    /** This class handles camera and texture region.
     * @author realitix
     */
    class LightProperties(var camera: Camera) {
        var region = TextureRegion()
    }

    /** This class handles LightProperties for each side of PointLight.
     * @author realitix
     */
    class PointLightProperties {
        var properties = ObjectMap<CubemapSide, LightProperties>(6)
    }

    /** Initialize framebuffers and shader providers. You should call super.init() in subclass.  */
    override fun init() {
        frameBuffers = Array(passQuantity)
        passShaderProviders = Array(passQuantity)

        for (i in 0 until passQuantity) {
            init(i)
        }
    }

    /** Initialize pass n  */
    protected abstract fun init(n: Int)

    override fun getPassShaderProvider(n: Int): ShaderProvider {
        return passShaderProviders!![n]
    }

    override fun addLight(light: NhgLight) {
        when (light.type) {
            LightType.POINT_LIGHT -> addLight(light, EnumSet.of(
                    CubemapSide.PositiveX, CubemapSide.NegativeX, CubemapSide.PositiveY,
                    CubemapSide.NegativeY, CubemapSide.PositiveZ, CubemapSide.NegativeZ))

            LightType.DIRECTIONAL_LIGHT -> {
                val directionalCamera = OrthographicCamera()
                directionalCamera.direction.set(light.direction)
                directionalCamera.near = 0.1f
                directionalCamera.far = 10f

                directionalCameras.put(light, LightProperties(directionalCamera))
            }

            LightType.SPOT_LIGHT -> {
                val spotCamera = PerspectiveCamera(light.outerAngle * 2f, 0f, 0f)
                spotCamera.position.set(light.position)
                spotCamera.direction.set(light.direction)
                spotCamera.near = 0.1f
                spotCamera.far = 10f
                spotCamera.up.set(spotCamera.direction.y, spotCamera.direction.z, spotCamera.direction.x)

                spotCameras.put(light, LightProperties(spotCamera))
            }
        }
    }

    override fun addLight(point: NhgLight, sides: Set<CubemapSide>) {
        val plProperty = PointLightProperties()
        for (i in 0..5) {
            val cubemapSide = CubemapSide.values()[i]
            if (sides.contains(cubemapSide)) {
                val camera = PerspectiveCamera(90f, 0f, 0f)
                camera.position.set(point.position)
                camera.direction.set(cubemapSide.direction)
                camera.up.set(cubemapSide.up)
                camera.near = 0.1f
                camera.far = 10f

                val p = LightProperties(camera)
                plProperty.properties.put(cubemapSide, p)
            }
        }
        pointCameras.put(point, plProperty)
    }

    override fun removeLight(light: NhgLight) {
        when (light.type) {
            LightType.SPOT_LIGHT -> spotCameras.remove(light)

            LightType.DIRECTIONAL_LIGHT -> directionalCameras.remove(light)

            LightType.POINT_LIGHT -> pointCameras.remove(light)
        }
    }

    override fun hasLight(light: NhgLight): Boolean {
        when (light.type) {
            LightType.POINT_LIGHT -> if (pointCameras.containsKey(light)) return true

            LightType.DIRECTIONAL_LIGHT -> if (directionalCameras.containsKey(light)) return true

            LightType.SPOT_LIGHT -> if (spotCameras.containsKey(light)) return true
        }

        return false
    }

    override fun update() {
        for (e in spotCameras) {
            // Reset camera
            e.value.camera.position.set(Vector3.Zero)
            e.value.camera.direction.set(0f, 0f, -1f)
            e.value.camera.up.set(0f, 1f, 0f)

            // Look at the direction of the light
            e.value.camera.lookAt(e.key.direction)

            // Set at the light's position
            e.value.camera.position.set(e.key.position)

            nearFarAnalyzer.analyze(e.key, e.value.camera, renderableProviders!!)
        }

        for (e in directionalCameras) {
            directionalAnalyzer.analyze(e.key, e.value.camera, camera!!).update()
        }

        for (e in pointCameras) {
            for (c in e.value.properties) {
                c.value.camera.position.set(e.key.position)
                nearFarAnalyzer.analyze(e.key, c.value.camera, renderableProviders!!)
            }
        }
    }

    override fun <T : RenderableProvider> begin(camera: Camera, renderableProviders: Iterable<T>) {
        if (this.renderableProviders != null || this.camera != null) throw GdxRuntimeException("Call end() first.")

        this.camera = camera
        this.renderableProviders = renderableProviders
    }

    override fun begin(n: Int) {
        if (n >= passShaderProviders?.size ?: Int.MAX_VALUE)
            throw GdxRuntimeException("Pass " + n + " doesn't exist in " + javaClass.name)

        currentPass = n
        spotCameraIterator = spotCameras.iterator()
        dirCameraIterator = directionalCameras.iterator()
        pointCameraIterator = pointCameras.iterator()
        currentPointSide = 6

        beginPass(n)
    }

    /** Begin pass n.
     * @param n Pass number
     */
    protected open fun beginPass(n: Int) {
        frameBuffers!![n].begin()
    }

    override fun end() {
        this.camera = null
        this.renderableProviders = null
        currentPass = -1
    }

    override fun end(n: Int) {
        if (currentPass != n) throw GdxRuntimeException("Begin $n must be called before end $n")
        endPass(n)
    }

    /** End pass n.
     * @param n Pass number
     */
    protected open fun endPass(n: Int) {
        frameBuffers!![n].end()
    }

    override fun next(): Camera? {
        var lp = nextDirectional()
        if (lp != null) return interceptCamera(lp)

        lp = nextSpot()
        if (lp != null) return interceptCamera(lp)

        lp = nextPoint()
        return if (lp != null) interceptCamera(lp) else null

    }

    /** Allows to return custom camera if needed.
     * @param lp Returned LightProperties
     * @return Camera
     */
    protected open fun interceptCamera(lp: LightProperties): Camera? {
        return lp.camera
    }

    protected fun nextDirectional(): LightProperties? {
        if (dirCameraIterator?.hasNext() == false) return null

        val e = dirCameraIterator?.next()
        currentLight = e?.key
        currentLightProperties = e?.value
        val lp = e?.value
        processViewport(lp!!, false)
        return lp
    }

    protected fun nextSpot(): LightProperties? {
        if (spotCameraIterator?.hasNext() == false) return null

        val e = spotCameraIterator?.next()
        currentLight = e?.key
        currentLightProperties = e?.value
        val lp = e?.value

        if (!lightFilter.filter(spotCameras.findKey(lp, true), lp?.camera!!, this.camera!!)) {
            return nextSpot()
        }

        processViewport(lp, true)
        return lp
    }

    protected fun nextPoint(): LightProperties? {
        if (pointCameraIterator?.hasNext() == false && currentPointSide > 5) return null

        if (currentPointSide > 5) currentPointSide = 0

        if (currentPointSide == 0) {
            val e = pointCameraIterator?.next()
            currentLight = e?.key
            currentPointProperties = e?.value
        }

        if (currentPointProperties?.properties?.containsKey(CubemapSide.values()[currentPointSide]) == true) {
            val lp = currentPointProperties?.properties?.get(CubemapSide.values()[currentPointSide])
            currentLightProperties = lp
            currentPointSide += 1

            if (!lightFilter.filter(pointCameras.findKey(currentPointProperties, true), lp?.camera!!, this.camera!!)) {
                return nextPoint()
            }

            processViewport(lp, true)
            return lp
        }

        currentPointSide += 1
        return nextPoint()
    }

    /** Set viewport according to allocator.
     * @param lp LightProperties to process.
     * @param cameraViewport Set camera viewport if true.
     */
    protected open fun processViewport(lp: LightProperties, cameraViewport: Boolean) {
        val camera = lp.camera
        val r = allocator.nextResult(currentLight!!) ?: return

        val region = lp.region
        region.texture = frameBuffers!![currentPass].colorBufferTexture

        // We don't use HdpiUtils
        // gl commands related to shadow map size and not to screen size
        Gdx.gl.glViewport(r.x, r.y, r.width, r.height)
        Gdx.gl.glScissor(r.x + 1, r.y + 1, r.width - 2, r.height - 2)
        region.setRegion(r.x, r.y, r.width, r.height)

        if (cameraViewport) {
            camera.viewportHeight = r.height.toFloat()
            camera.viewportWidth = r.width.toFloat()
            camera.update()
        }
    }

    fun getTexture(n: Int): Texture {
        if (n >= passQuantity) throw GdxRuntimeException("Can't get texture $n")
        return frameBuffers!![n].colorBufferTexture
    }

    override fun dispose() {
        for (i in 0 until passQuantity) {
            frameBuffers!![i].dispose()
            passShaderProviders!![i].dispose()
        }
    }
}
/** Construct the system with default values  */
