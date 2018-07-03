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

import io.github.movementspeed.nhglib.files.gltf.jgltf.model.gl.ProgramModel;
import io.github.movementspeed.nhglib.files.gltf.jgltf.model.gl.ShaderModel;
import io.github.movementspeed.nhglib.files.gltf.jgltf.model.impl.AbstractNamedModelElement;

import java.util.Objects;

/**
 * Implementation of a {@link ProgramModel}
 */
public class DefaultProgramModel extends AbstractNamedModelElement
        implements ProgramModel {
    /**
     * The vertex shader model
     */
    private ShaderModel vertexShaderModel;

    /**
     * The fragment shader model
     */
    private ShaderModel fragmentShaderModel;

    /**
     * Default constructor
     */
    public DefaultProgramModel() {
        // Default constructor
    }

    /**
     * Set the vertex {@link ShaderModel}
     *
     * @param vertexShaderModel The vertex {@link ShaderModel}
     */
    public void setVertexShaderModel(ShaderModel vertexShaderModel) {
        this.vertexShaderModel = Objects.requireNonNull(vertexShaderModel,
                "The vertexShaderModel may not be null");
    }

    @Override
    public ShaderModel getVertexShaderModel() {
        return vertexShaderModel;
    }

    /**
     * Set the fragment {@link ShaderModel}
     *
     * @param fragmentShaderModel The fragment {@link ShaderModel}
     */
    public void setFragmentShaderModel(ShaderModel fragmentShaderModel) {
        this.fragmentShaderModel = Objects.requireNonNull(fragmentShaderModel,
                "The fragmentShaderModel may not be null");
    }

    @Override
    public ShaderModel getFragmentShaderModel() {
        return fragmentShaderModel;
    }
}

