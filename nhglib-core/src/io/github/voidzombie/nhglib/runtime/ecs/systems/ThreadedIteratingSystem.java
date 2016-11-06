package io.github.voidzombie.nhglib.runtime.ecs.systems;

import com.artemis.Aspect;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.MathUtils;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.runtime.threading.Threading;
import io.github.voidzombie.nhglib.runtime.threading.Work;

import java.util.Arrays;

/**
 * Created by Fausto Napoli on 02/11/2016.
 */
public abstract class ThreadedIteratingSystem extends NhgBaseSystem {
    private int split;
    private int rows;
    private int[][] splitEntities;

    public ThreadedIteratingSystem(Aspect.Builder aspect) {
        super(aspect);
    }

    @Override
    protected final void processSystem() {
        IntBag actives = subscription.getEntities();
        int activesSize = actives.size();

        int previousSplit = split;
        split = MathUtils.ceil((float) actives.size() / (float) Threading.cores);

        int previousRows = rows;
        if (activesSize > Threading.cores) {
            rows = Threading.cores;
        } else {
            rows = activesSize;
        }

        if (previousRows != rows) {
            NHG.threading.setLatchCount(rows);
        }

        if (previousSplit != split) {
            splitEntities = new int[rows][split];

            int from;
            int to;

            for (int i = 0; i < rows; i++) {
                if (split == 1) {
                    splitEntities[i][0] = actives.getData()[i];
                } else {
                    from = i * split;
                    to = from + split;

                    splitEntities[i] = Arrays.copyOfRange(actives.getData(), from, to);
                }

                NHG.logger.log(this, "%d", 1);
            }
        }

        for (int i = 0; i < rows; i++) {
            NHG.threading.execute(new ProcessWork(splitEntities[i]));
        }

        NHG.threading.await();
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
                    if (e != -1) {
                        process(e);
                    }
                }
            }

            NHG.threading.countDown();
        }
    }
}