package io.github.voidzombie.nhglib.graphics.interfaces;

import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Matrix4;

/**
 * Created by Fausto Napoli on 09/12/2016.
 */
public interface Representation {
    void setTransform(Matrix4 transform);
    void setTranslation(float x, float y, float z);
    void setRotation(float x, float y, float z);
    void setScale(float x, float y, float z);
    void invalidate();
    RenderableProvider get();
}
