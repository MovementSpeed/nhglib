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

package io.github.movementspeed.nhglib.graphics.shaders.shadows.utils

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.RenderableProvider
import com.badlogic.gdx.graphics.g3d.environment.BaseLight
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Pool

/** Compute near and far plane based on renderable providers passed in constructor. Renderable providers array should contains only
 * renderable in camera frustum.
 * @author realitix
 */
class AABBNearFarAnalyzer : NearFarAnalyzer {

    protected val renderablesPool = RenderablePool()
    /** list of Renderables to be rendered in the current batch  */
    protected val renderables = Array<Renderable>()

    /** Objects used for computation  */
    protected var bb1 = BoundingBox()
    protected var tmpV = Vector3()

    // @TODO Merge renderable pools (ModelBatch)
    protected class RenderablePool : Pool<Renderable>() {
        protected var obtained = Array<Renderable>()

        override fun newObject(): Renderable {
            return Renderable()
        }

        override fun obtain(): Renderable {
            val renderable = super.obtain()
            renderable.environment = null
            renderable.material = null
            renderable.meshPart.set("", null, 0, 0, 0)
            renderable.shader = null
            obtained.add(renderable)
            return renderable
        }

        fun flush() {
            super.freeAll(obtained)
            obtained.clear()
        }
    }

    override fun <T : RenderableProvider> analyze(light: BaseLight<*>, camera: Camera, renderableProviders: Iterable<T>) {
        getRenderables(renderableProviders)
        prepareCamera(camera)

        bb1.inf()
        for (renderable in renderables) {
            renderable.worldTransform.getTranslation(tmpV)
            tmpV.add(renderable.meshPart.center)

            if (camera.frustum.sphereInFrustum(tmpV, renderable.meshPart.radius)) {
                bb1.ext(tmpV, renderable.meshPart.radius)
            }
        }

        computeResult(bb1, camera)
        renderablesPool.flush()
        renderables.clear()
    }

    protected fun <T : RenderableProvider> getRenderables(renderableProviders: Iterable<T>) {
        for (renderableProvider in renderableProviders) {
            renderableProvider.getRenderables(renderables, renderablesPool)
        }
    }

    /** Initialize camera before computation.
     * @param camera Camera to compute.
     */
    protected fun prepareCamera(camera: Camera) {
        camera.near = AABBNearFarAnalyzer.CAMERA_NEAR
        camera.far = AABBNearFarAnalyzer.CAMERA_FAR
        camera.update()
    }

    /** Compute final result.
     * @param bb BoundingBox encompassing instances
     * @param camera Camera to compute
     */
    protected fun computeResult(bb: BoundingBox, camera: Camera) {
        // Radius
        val radius = bb1.getDimensions(tmpV).len() * 0.5f

        // Center
        bb1.getCenter(tmpV)

        // Computation
        val distance = tmpV.dst(camera.position)
        var near = distance - radius
        var far = distance + radius

        if (near <= 0) near = CAMERA_NEAR
        if (far <= 0) far = CAMERA_FAR

        camera.near = near
        camera.far = far
        camera.update()
    }

    companion object {
        /** Near and far initialization before computation. You should put the same values as the main camera  */
        var CAMERA_NEAR = 1f
        var CAMERA_FAR = 100.0f
    }
}
