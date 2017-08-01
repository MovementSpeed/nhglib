package io.github.voidzombie.nhglib.runtime.ecs.components.graphics;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import io.github.voidzombie.nhglib.assets.Assets;
import io.github.voidzombie.nhglib.graphics.particles.ParticleEffectProvider;

public class ParticleEffectComponent extends Component {
    public boolean added;

    public String asset;
    public State state;
    public ParticleEffect particleEffect;

    public ParticleEffectComponent() {
        state = State.NOT_INITIALIZED;
    }

    public void build(Assets assets, ParticleEffectProvider particleEffectProvider) {
        ParticleEffect particleEffect = assets.get(asset);
        particleEffectProvider.addParticleEffect(asset, particleEffect);

        this.particleEffect = particleEffectProvider.obtainParticleEffect(asset);
        this.particleEffect.init();
    }

    public enum State {
        NOT_INITIALIZED,
        READY
    }
}
