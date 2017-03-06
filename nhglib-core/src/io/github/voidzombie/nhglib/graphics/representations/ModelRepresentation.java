package io.github.voidzombie.nhglib.graphics.representations;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import io.github.voidzombie.nhglib.graphics.interfaces.Representation;

/**
 * Created by Fausto Napoli on 09/12/2016.
 */
public class ModelRepresentation implements Representation {
    private Quaternion rotation;
    private Vector3 translation;
    private Vector3 scale;

    private ModelInstance modelInstance;
    private AnimationController animationController;

    public ModelRepresentation(Model model) {
        if (model != null) {
            this.modelInstance = new ModelInstance(model);

            if (modelInstance.animations.size > 0) {
                animationController = new AnimationController(modelInstance);
            }
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
    public void invalidate() {
        modelInstance = null;
        animationController = null;
    }

    public ModelInstance getModelInstance() {
        return modelInstance;
    }

    public AnimationController getAnimationController() {
        return animationController;
    }
}
