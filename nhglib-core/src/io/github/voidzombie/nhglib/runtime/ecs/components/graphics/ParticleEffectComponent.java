package io.github.voidzombie.nhglib.runtime.ecs.components.graphics;

import com.artemis.PooledComponent;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import io.github.voidzombie.nhglib.assets.Asset;

public class ParticleEffectComponent extends PooledComponent {
    public boolean added;

    public Asset asset;
    public ParticleEffect particleEffect;

    @Override
    protected void reset() {

    }
}
