/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package io.github.movementspeed.nhglib.graphics.shaders.shadows.system;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Cubemap.CubemapSide;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import io.github.movementspeed.nhglib.graphics.lights.NhgLight;

import java.util.Set;

/** Shadow system provides functionalities to render shadows.
 * <p>
 * Typical use: <br />
 *
 * <pre>
 * // Init system:
 * Array&lt;ModelBatch&gt; passBatches = new Array&lt;ModelBatch&gt;();
 * ModelBatch mainBatch;
 * ShadowSystem system = new XXXShadowSystem();
 * system.init();
 * for (int i = 0; i &lt; system.getPassQuantity(); i++) {
 * 	passBatches.add(new ModelBatch(system.getPassShaderProvider(i)));
 * }
 * mainBatch = new ModelBatch(system.getShaderProvider());
 *
 * // Render scene with shadows:
 * system.begin(camera, instances);
 * system.update();
 * for (int i = 0; i &lt; system.getPassQuantity(); i++) {
 * 	system.begin(i);
 * 	Camera camera;
 * 	while ((camera = system.next()) != null) {
 * 		passBatches.get(i).begin(camera);
 * 		passBatches.get(i).render(instances, environment);
 * 		passBatches.get(i).end();
 * 	}
 * 	camera = null;
 * 	system.end(i);
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
 * </pre>
 *
 * </p>
 *
 * <p>
 * Current environnment should be alway be synchonized with shadow system lights. It means that if you add or remove light from
 * environment, you should do it in shadow system too. <br />
 * If you have two different environments, when you switch, you should add and remove all lights in shadow system.
 * </p>
 * @author realitix */
public interface ShadowSystem {

	/** Initialize system */
	void init();

	/** Return number of pass
	 * @return int */
	int getPassQuantity();

	/** Return shaderProvider of the pass n
	 * @return ShaderProvider */
	ShaderProvider getPassShaderProvider(int n);

	void addLight(NhgLight light);

	/** Add point light in shadow system
	 * @param point PointLight to add in the ShadowSystem
	 * @param sides Set of side */
	void addLight(NhgLight point, Set<CubemapSide> sides);

	/** Remove light from the shadowSystem
	 * @param light Light to remove in the ShadowSystem */
	void removeLight(NhgLight light);

	/** @param light Light to check
	 * @return true if light analyzed */
	boolean hasLight(NhgLight light);

	/** Update shadowSystem */
	void update();

	/** Begin shadow system with main camera and renderable providers.
	 * @param camera
	 * @param renderableProviders */
	<T extends RenderableProvider> void begin(Camera camera, Iterable<T> renderableProviders);

	/** Begin pass n rendering.
	 * @param n Pass number */
	void begin(int n);

	/** Switch light
	 * @return Current camera */
	Camera next();

	/** End shadow system */
	void end();

	/** End pass n rendering */
	void end(int n);
}
