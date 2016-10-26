package io.github.voidzombie.nhglib.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;

/**
 * Created by Fausto Napoli on 26/10/2016.
 */
public class DefaultPerspectiveCamera extends PerspectiveCamera {
    public DefaultPerspectiveCamera() {
        fieldOfView = 67;

        viewportWidth = Gdx.graphics.getWidth();
        viewportHeight = Gdx.graphics.getHeight();

        near = 0.01f;
        far = 100.0f;

        update();
    }
}
