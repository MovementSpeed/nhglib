package io.github.voidzombie.nhglib.graphics.particles;

import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.utils.Pool;

public class ParticleEffectPool extends Pool<ParticleEffect> {
    private ParticleEffect originalEffect;

    public ParticleEffectPool(ParticleEffect originalEffect) {
        this.originalEffect = originalEffect;
    }

    @Override
    public void free(ParticleEffect object) {
        object.reset();
        super.free(object);
    }

    @Override
    protected ParticleEffect newObject() {
        return originalEffect.copy();
    }
}
