/*
 * www.javagl.de - JglTF
 *
 * Copyright 2015-2017 Marco Hutter - http://www.javagl.de
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package io.github.movementspeed.nhglib.files.gltf.jgltf.model.gl.impl;

import io.github.movementspeed.nhglib.files.gltf.jgltf.impl.v1.TechniqueStatesFunctions;
import io.github.movementspeed.nhglib.files.gltf.jgltf.model.GltfConstants;
import io.github.movementspeed.nhglib.files.gltf.jgltf.model.gl.TechniqueStatesFunctionsModel;
import io.github.movementspeed.nhglib.files.gltf.jgltf.model.gl.TechniqueStatesModel;
import io.github.movementspeed.nhglib.files.gltf.jgltf.model.gl.impl.v1.DefaultTechniqueStatesFunctionsModelV1;

import java.util.Arrays;
import java.util.List;

/**
 * Methods to create {@link TechniqueStatesModel} instances
 */
public class TechniqueStatesModels {
    /**
     * Create a default {@link TechniqueStatesModel}
     *
     * @return The {@link TechniqueStatesModel}
     */
    public static TechniqueStatesModel createDefault() {
        // This implementation is backed by the V1 implementation of the
        // technique states functions, but this will not be visible for
        // the caller
        List<Integer> enable = Arrays.asList(
                GltfConstants.GL_DEPTH_TEST,
                GltfConstants.GL_CULL_FACE
        );
        TechniqueStatesFunctions functions =
                io.github.movementspeed.nhglib.files.gltf.jgltf.model.v1.gl.Techniques
                        .createDefaultTechniqueStatesFunctions();
        TechniqueStatesFunctionsModel techniqueStatesFunctionsModel =
                new DefaultTechniqueStatesFunctionsModelV1(functions);
        TechniqueStatesModel techniqueStatesModel =
                new DefaultTechniqueStatesModel(
                        enable, techniqueStatesFunctionsModel);
        return techniqueStatesModel;
    }

    /**
     * Private constructor to prevent instantiation
     */
    private TechniqueStatesModels() {
        // Private constructor to prevent instantiation
    }
}
