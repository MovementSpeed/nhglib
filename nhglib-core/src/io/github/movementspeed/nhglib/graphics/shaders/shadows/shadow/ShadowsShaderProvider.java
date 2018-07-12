package io.github.movementspeed.nhglib.graphics.shaders.shadows.shadow;

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

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider;
import com.badlogic.gdx.utils.Array;
import io.github.movementspeed.nhglib.graphics.lights.NhgLight;
import io.github.movementspeed.nhglib.utils.graphics.ShaderUtils;

public class ShadowsShaderProvider extends BaseShaderProvider {
    private Array<NhgLight> shadowLights;

    public ShadowsShaderProvider(Array<NhgLight> shadowLights) {
        this.shadowLights = shadowLights;
    }

    @Override
    protected Shader createShader(final Renderable renderable) {
        ShadowsShader.Params params = new ShadowsShader.Params();
        params.useBones = ShaderUtils.useBones(renderable);
        return new ShadowsShader(renderable, shadowLights, params);
    }
}
