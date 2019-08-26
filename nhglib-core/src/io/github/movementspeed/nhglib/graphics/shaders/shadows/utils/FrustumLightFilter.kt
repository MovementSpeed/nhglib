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
import com.badlogic.gdx.graphics.g3d.environment.BaseLight
import com.badlogic.gdx.math.Frustum
import com.badlogic.gdx.math.collision.BoundingBox

/** This Filter allows lights that are in camera frustum.
 * @author realitix
 */
class FrustumLightFilter : LightFilter {
    /** Bounding box used for computation  */
    protected var bb = BoundingBox()

    override fun filter(light: BaseLight<*>, camera: Camera, mainCamera: Camera): Boolean {
        val f1 = mainCamera.frustum
        val f2 = camera.frustum
        bb.inf()

        for (i in f2.planePoints.indices) {
            bb.ext(f2.planePoints[i])
        }

        return f1.boundsInFrustum(bb)
    }
}
