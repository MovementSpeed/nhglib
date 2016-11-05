package io.github.voidzombie.nhglib.runtime.ecs.systems;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.MathUtils;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.runtime.threading.Threading;
import io.github.voidzombie.nhglib.runtime.threading.Work;

import java.util.Arrays;

/**
 * Created by Fausto Napoli on 02/11/2016.
 */
public abstract class ThreadedIteratingSystem extends BaseEntitySystem {
    private int previousSplit;
    private int split;
    private int[][] splitEntities;


    public ThreadedIteratingSystem(Aspect.Builder aspect) {
        super(aspect);
    }

    @Override
    protected final void processSystem() {
        IntBag actives = subscription.getEntities();

        previousSplit = split;
        split = MathUtils.ceil((float) actives.size() / (float) Threading.cores);

        if (previousSplit != split) {
            splitEntities = new int[Threading.cores][split];

            for (int i = 0; i < Threading.cores; i++) {
                int from = i * (split + 1);
                int to = ((i + 1) * split);

                splitEntities[i] = Arrays.copyOfRange(actives.getData(), from, to);
            }
        }

        for (int i = 0; i < Threading.cores; i++) {
            NHG.threading.execute(new ProcessWork(splitEntities[i]));
        }

        NHG.threading.await(Threading.cores);
    }

    protected abstract void process(int entityId);

    private class ProcessWork extends Work {
        private int[] entitiesPart;

        ProcessWork(int[] entitiesPart) {
            this.entitiesPart = entitiesPart;
        }

        @Override
        public void run() {
            if (entitiesPart != null) {
                for (int e : entitiesPart) {
                    process(e);
                }

                NHG.threading.countDown();
            } else {
                NHG.threading.countDown();
            }
        }
    }
}