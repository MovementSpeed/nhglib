package io.github.voidzombie.nhglib.runtime.entry;

import com.artemis.WorldConfigurationBuilder;

/**
 * Created by Fausto Napoli on 26/11/2016.
 * Public entry point for games using this library.
 */
public class NHGEntry extends BaseGame {
    public NHGEntry() {
        init(this);
    }

    @Override
    public void engineStarted() {
        super.engineStarted();
    }

    @Override
    public void engineInitialized() {
        super.engineInitialized();
    }

    @Override
    public void engineUpdate() {
        super.engineUpdate();
    }

    @Override
    public void engineClosing() {
        super.engineClosing();
    }

    @Override
    public void onConfigureEntitySystems(WorldConfigurationBuilder configurationBuilder) {
        super.onConfigureEntitySystems(configurationBuilder);
    }
}
