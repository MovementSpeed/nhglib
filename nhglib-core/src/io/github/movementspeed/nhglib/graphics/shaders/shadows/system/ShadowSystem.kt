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

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Cubemap.CubemapSide
import com.badlogic.gdx.graphics.g3d.RenderableProvider
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider
import io.github.movementspeed.nhglib.graphics.lights.NhgLight

/** Shadow system provides functionalities to render shadows.
 *
 *
 * Typical use: <br></br>
 *
 * <pre>
 * // Init system:
 * Array&lt;ModelBatch&gt; passBatches = new Array&lt;ModelBatch&gt;();
 * ModelBatch mainBatch;
 * ShadowSystem system = new XXXShadowSystem();
 * system.init();
 * for (int i = 0; i &lt; system.getPassQuantity(); i++) {
 * passBatches.add(new ModelBatch(system.getPassShaderProvider(i)));
 * }
 * mainBatch = new ModelBatch(system.getShaderProvider());
 *
 * // Render scene with shadows:
 * system.begin(camera, instances);
 * system.update();
 * for (int i = 0; i &lt; system.getPassQuantity(); i++) {
 * system.begin(i);
 * Camera camera;
 * while ((camera = system.next()) != null) {
 * passBatches.get(i).begin(camera);
 * passBatches.get(i).render(instances, environment);
 * passBatches.get(i).end();
 * }
 * camera = null;
 * system.end(i);
 * }
 * system.end();
 *
 * HdpiUtils.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
 * Gdx.gl.glClearColor(0, 0, 0, 1);
 * Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
 *
 * mainBatch.begin(cam);
 * mainBatch.render(instances, environment);
 * mainBatch.end();
</pre> *
 *
 *
 *
 *
 *
 * Current environnment should be alway be synchonized with shadow system lights. It means that if you add or remove light from
 * environment, you should do it in shadow system too. <br></br>
 * If you have two different environments, when you switch, you should add and remove all lights in shadow system.
 *
 * @author realitix
 */
interface ShadowSystem {

    /** Return number of pass
     * @return int
     */
    val passQuantity: Int

    /** Initialize system  */
    fun init()

    /** Return shaderProvider of the pass n
     * @return ShaderProvider
     */
    fun getPassShaderProvider(n: Int): ShaderProvider

    fun addLight(light: NhgLight)

    /** Add point light in shadow system
     * @param point PointLight to add in the ShadowSystem
     * @param sides Set of side
     */
    fun addLight(point: NhgLight, sides: Set<CubemapSide>)

    /** Remove light from the shadowSystem
     * @param light Light to remove in the ShadowSystem
     */
    fun removeLight(light: NhgLight)

    /** @param light Light to check
     * @return true if light analyzed
     */
    fun hasLight(light: NhgLight): Boolean

    /** Update shadowSystem  */
    fun update()

    /** Begin shadow system with main camera and renderable providers.
     * @param camera
     * @param renderableProviders
     */
    fun <T : RenderableProvider> begin(camera: Camera, renderableProviders: Iterable<T>)

    /** Begin pass n rendering.
     * @param n Pass number
     */
    fun begin(n: Int)

    /** Switch light
     * @return Current camera
     */
    operator fun next(): Camera?

    /** End shadow system  */
    fun end()

    /** End pass n rendering  */
    fun end(n: Int)
}
