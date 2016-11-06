package io.github.voidzombie.nhglib.interfaces;

/**
 * Created by Fausto Napoli on 02/11/2016.
 */
public interface EngineStateListener {
    void onEngineStart();

    void onEngineInitialized();

    void onEngineRunning();

    void onEnginePaused();
}
