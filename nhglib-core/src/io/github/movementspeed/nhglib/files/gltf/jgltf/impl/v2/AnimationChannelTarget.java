/*
 * glTF JSON model
 * 
 * Do not modify this class. It is automatically generated
 * with JsonModelGen (https://github.com/javagl/JsonModelGen)
 * Copyright (c) 2016 Marco Hutter - http://www.javagl.de
 */

package io.github.movementspeed.nhglib.files.gltf.jgltf.impl.v2;



/**
 * The index of the node and TRS property that an animation channel 
 * targets. 
 * 
 * Auto-generated for animation.channel.target.schema.json 
 * 
 */
public class AnimationChannelTarget
    extends GlTFProperty
{

    /**
     * The index of the node to target. (optional) 
     * 
     */
    private Integer node;
    /**
     * The name of the node's TRS property to modify, or the "weights" of the 
     * Morph Targets it instantiates. (required)<br> 
     * Valid values: ["translation", "rotation", "scale", "weights"] 
     * 
     */
    private String path;

    /**
     * The index of the node to target. (optional) 
     * 
     * @param node The node to set
     * 
     */
    public void setNode(Integer node) {
        if (node == null) {
            this.node = node;
            return ;
        }
        this.node = node;
    }

    /**
     * The index of the node to target. (optional) 
     * 
     * @return The node
     * 
     */
    public Integer getNode() {
        return this.node;
    }

    /**
     * The name of the node's TRS property to modify, or the "weights" of the 
     * Morph Targets it instantiates. (required)<br> 
     * Valid values: ["translation", "rotation", "scale", "weights"] 
     * 
     * @param path The path to set
     * @throws NullPointerException If the given value is <code>null</code>
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setPath(String path) {
        if (path == null) {
            throw new NullPointerException((("Invalid value for path: "+ path)+", may not be null"));
        }
        if ((((!"translation".equals(path))&&(!"rotation".equals(path)))&&(!"scale".equals(path)))&&(!"weights".equals(path))) {
            throw new IllegalArgumentException((("Invalid value for path: "+ path)+", valid: [\"translation\", \"rotation\", \"scale\", \"weights\"]"));
        }
        this.path = path;
    }

    /**
     * The name of the node's TRS property to modify, or the "weights" of the 
     * Morph Targets it instantiates. (required)<br> 
     * Valid values: ["translation", "rotation", "scale", "weights"] 
     * 
     * @return The path
     * 
     */
    public String getPath() {
        return this.path;
    }

}
