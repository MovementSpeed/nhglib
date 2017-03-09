package io.github.voidzombie.nhglib.runtime.ecs.systems.base;

import com.artemis.Aspect;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.MathUtils;
import io.github.voidzombie.nhglib.Nhg;
import io.github.voidzombie.nhglib.runtime.threading.Threading;
import io.github.voidzombie.nhglib.runtime.threading.Work;

import java.util.Arrays;

/**
 * Created by Fausto Napoli on 02/11/2016.
 */
public abstract class ThreadedIteratingSystem extends NhgBaseEntitySystem {
    private int split;
    private int rows;
    private int latchId;
    private int[][] splitEntities;

    public ThreadedIteratingSystem(Aspect.Builder aspect) {
        super(aspect);
        latchId = 2165;
        Nhg.threading.createLatch(latchId, Threading.cores);
    }

    @Override
    protected final void processSystem() {
        IntBag actives = subscription.getEntities();
        int activesSize = actives.size();

        if (activesSize > 0) {
            int previousSplit = split;
            split = MathUtils.ceil((float) actives.size() / (float) Threading.cores);

            int previousRows = rows;
            if (activesSize > Threading.cores) {
                rows = Threading.cores;
            } else {
                rows = activesSize;
            }

            if (previousRows != rows) {
                Nhg.threading.setLatchCount(latchId, rows);
            }

            if (previousSplit != split) {
                splitEntities = new int[rows][split];

                int from;
                int to;
                int[] data = actives.getData();

                for (int i = 0; i < rows; i++) {
                    if (split == 1) {
                        splitEntities[i][0] = data[i];
                    } else {
                        from = i * split;
                        to = from + split;

                        splitEntities[i] = Arrays.copyOfRange(data, from, to);
                    }

                    if (i > 0) {
                        postProcessList(splitEntities[i]);
                    }
                }
            }

            if (rows > splitEntities.length) {
                rows = splitEntities.length;
            }

            for (int i = 0; i < rows; i++) {
                Nhg.threading.execute(new ProcessWork(splitEntities[i]));
            }

            Nhg.threading.awaitLatch(latchId);
        }
    }

    protected abstract void process(int entityId);

    private void postProcessList(int[] list) {
        for (int i = 0; i < list.length; i++) {
            int entity = list[i];

            if (entity == 0) {
                list[i] = -1;
            }
        }
    }

    private class ProcessWork extends Work {
        private int[] entitiesPart;

        ProcessWork(int[] entitiesPart) {
            this.entitiesPart = entitiesPart;
        }

        @Override
        public void run() {
            if (entitiesPart != null) {
                for (int entity : entitiesPart) {
                    if (entity != -1) {
                        process(entity);
                    }
                }
            }

            Nhg.threading.countDownLatch(latchId);
        }
    }
}