package io.github.movementspeed.nhglib.core.entry;

import com.artemis.BaseSystem;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Fausto Napoli on 26/11/2016.
 * Public entry point for games using this library.
 */
public class NhgEntry extends BaseGame {
    public NhgEntry() {
        init(this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
    }

    @Override
    public void onUpdate(float delta) {
        super.onUpdate(delta);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    @Override
    public void onResize(int width, int height) {
        super.onResize(width, height);
    }

    @Override
    public void onDispose() {
        super.onDispose();
    }

    @Override
    public Array<BaseSystem> onConfigureEntitySystems() {
        return super.onConfigureEntitySystems();
    }
}
