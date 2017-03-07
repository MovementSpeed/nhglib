package io.github.voidzombie.nhglib.runtime.entry;

import com.artemis.WorldConfigurationBuilder;

/**
 * Created by Fausto Napoli on 26/11/2016.
 * Public entry point for games using this library.
 */
public class NhgEntry extends BaseGame {
    public NhgEntry() {
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
    public void engineUpdate(float delta) {
        super.engineUpdate(delta);
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