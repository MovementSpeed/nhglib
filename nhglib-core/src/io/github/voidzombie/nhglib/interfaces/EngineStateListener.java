package io.github.voidzombie.nhglib.interfaces;

/**
 * Created by Fausto Napoli on 02/11/2016.
 */
public interface EngineStateListener {
    void engineStarted();
    void engineInitialized();
    void engineUpdate();
    void enginePaused();
    void engineClosing();
}
