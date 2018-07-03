/*
 * glTF JSON model
 *
 * Do not modify this class. It is automatically generated
 * with JsonModelGen (https://github.com/javagl/JsonModelGen)
 * Copyright (c) 2016 Marco Hutter - http://www.javagl.de
 */

package io.github.movementspeed.nhglib.files.gltf.jgltf.impl.v2;


/**
 * Auto-generated for material.normalTextureInfo.schema.json
 */
public class MaterialNormalTextureInfo
        extends TextureInfo {

    /**
     * The scalar multiplier applied to each normal vector of the normal
     * texture. (optional)<br>
     * Default: 1.0
     */
    private Float scale;

    /**
     * The scalar multiplier applied to each normal vector of the normal
     * texture. (optional)<br>
     * Default: 1.0
     *
     * @param scale The scale to set
     */
    public void setScale(Float scale) {
        if (scale == null) {
            this.scale = scale;
            return;
        }
        this.scale = scale;
    }

    /**
     * The scalar multiplier applied to each normal vector of the normal
     * texture. (optional)<br>
     * Default: 1.0
     *
     * @return The scale
     */
    public Float getScale() {
        return this.scale;
    }

    /**
     * Returns the default value of the scale<br>
     *
     * @return The default scale
     * @see #getScale
     */
    public Float defaultScale() {
        return 1.0F;
    }

}
