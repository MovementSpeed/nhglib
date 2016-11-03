package io.github.voidzombie.nhglib.runtime.ecs.systems;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.utils.IntBag;
import com.google.common.collect.Lists;
import io.github.voidzombie.nhglib.NHG;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fausto Napoli on 02/11/2016.
 */
public abstract class ThreadedIteratingSystem extends BaseEntitySystem {
    private final int cores = Runtime.getRuntime().availableProcessors();

    private List<Integer> entities = new ArrayList<Integer>();
    private List<List<Integer>> splitEntities;
    private List<MyThread> threads = new ArrayList<MyThread>();

    public ThreadedIteratingSystem(Aspect.Builder aspect) {
        super(aspect);

        for (int i = 0; i < cores; i++) {
            MyThread t = new MyThread(i);
            t.start();

            threads.add(t);
        }
    }

    @Override
    protected final void processSystem() {
        entities.clear();
        IntBag actives = subscription.getEntities();

        for (int i = 0; i < actives.size(); i++) {
            int en = actives.get(i);
            entities.add(en);
        }

        resetThreads();
        splitEntities = Lists.partition(entities, entities.size() / cores);

        for (int i = 0; i < splitEntities.size(); i++) {
            List<Integer> integers = new ArrayList<Integer>(splitEntities.get(i));

            MyThread t = threads.get(i);
            t.setTarget(new MyRunnable(integers));
        }
    }

    protected abstract void process(int entityId);

    private void resetThreads() {
        for (MyThread t : threads) {
            t.setTarget(null);
        }
    }

    private class MyThread extends Thread {
        private int seqId;
        private MyRunnable target;

        public MyThread(int seqId) {
            this.seqId = seqId;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(16);

                    if (target != null) {
                        NHG.logger.log(this, "Thread id: %d, timestamp: %d", seqId, System.currentTimeMillis());
                        target.run();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        void setTarget(MyRunnable target) {
            this.target = target;
        }
    }

    private class MyRunnable implements Runnable {
        private List<Integer> integers;

        MyRunnable(List<Integer> integers) {
            this.integers = integers;
        }

        @Override
        public void run() {
            if (integers != null) {
                for (Integer i : integers) {
                    process(i);
                }
            }
        }
    }
}
