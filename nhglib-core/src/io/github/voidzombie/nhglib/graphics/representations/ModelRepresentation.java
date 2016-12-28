package io.github.voidzombie.nhglib.graphics.representations;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import io.github.voidzombie.nhglib.graphics.interfaces.Representation;

/**
 * Created by Fausto Napoli on 09/12/2016.
 */
public class ModelRepresentation implements Representation {
    private Boolean cached;
    private ModelInstance modelInstance;

    private Quaternion rotation;
    private Vector3 translation;
    private Vector3 scale;

    public ModelRepresentation(Model model) {
        cached = false;

        if (model != null) {
            this.modelInstance = new ModelInstance(model);
        }

        translation = new Vector3();
        rotation = new Quaternion();
        scale = new Vector3();
    }

    @Override
    public RenderableProvider get() {
        return modelInstance;
    }

    @Override
    public void setTransform(Matrix4 transform) {
        if (modelInstance != null && transform != null) {
            modelInstance.transform.set(transform);
        }
    }

    @Override
    public void setTranslation(float x, float y, float z) {
        if (modelInstance != null) {
            translation.set(x, y, z);
            modelInstance.transform.getRotation(rotation);
            modelInstance.transform.getScale(scale);

            modelInstance.transform.set(translation, rotation, scale);
        }
    }

    @Override
    public void setRotation(float x, float y, float z) {
        if (modelInstance != null) {
            modelInstance.transform.getTranslation(translation);
            rotation.setEulerAngles(y, x, z);
            modelInstance.transform.getScale(scale);

            modelInstance.transform.set(translation, rotation, scale);
        }
    }

    @Override
    public void setScale(float x, float y, float z) {
        if (modelInstance != null) {
            modelInstance.transform.getTranslation(translation);
            modelInstance.transform.getRotation(rotation);
            scale.set(x, y, z);

            modelInstance.transform.set(translation, rotation, scale);
        }
    }

    @Override
    public void setCached(Boolean cached) {
        this.cached = cached;
    }

    @Override
    public Boolean isCached() {
        return cached;
    }
}
