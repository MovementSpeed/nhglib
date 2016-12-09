package io.github.voidzombie.nhglib.runtime.fsm.interfaces;

/**
 * Created by Fausto Napoli on 02/11/2016.
 */
public interface EngineStateListener {
    void engineStarted();
    void engineInitialized();
    void engineUpdate();
    void engineClosing();
}
