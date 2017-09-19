package io.github.movementspeed.nhglib.runtime.fsm.interfaces;

/**
 * Created by Fausto Napoli on 02/11/2016.
 */
public interface EngineStateListener {
    void onStart();

    void onInitialized();

    void onUpdate(float delta);

    void onPause();

    void onClose();

    void onResize(int width, int height);

    void onDispose();
}
