package io.github.movementspeed.nhglib.graphics.particles;

import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.utils.ArrayMap;

public class ParticleEffectProvider {
    private ArrayMap<String, ParticleEffectPool> particleEffectPoolMap;

    public ParticleEffectProvider() {
        particleEffectPoolMap = new ArrayMap<>();
    }

    public void addParticleEffect(String asset, ParticleEffect effect) {
        if (!particleEffectPoolMap.containsKey(asset)) {
            ParticleEffectPool particleEffectPool = new ParticleEffectPool(effect);
            particleEffectPoolMap.put(asset, particleEffectPool);
        }
    }

    public ParticleEffect obtainParticleEffect(String asset) {
        return particleEffectPoolMap.get(asset).obtain();
    }
}
