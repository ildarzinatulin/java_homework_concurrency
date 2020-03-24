import java.util.ArrayList;
import java.util.LinkedList;

public class ScalableThreadPool implements ThreadPool{
    private final ArrayList<PoolWorker> threads;
    private final int minPoolSize;
    private final int maxPoolSize;
    private final LinkedList<Runnable> tasks;

    public ScalableThreadPool(int minPoolSize, int maxPoolSize) {
        this.minPoolSize = minPoolSize;
        this.maxPoolSize = maxPoolSize;
        tasks = new LinkedList<Runnable>();
        threads = new ArrayList<PoolWorker>(minPoolSize);
    }

    public void start() {
        for (PoolWorker thread : threads) {
            thread.start();
        }
    }

    public synchronized void execute(Runnable runnable) {
        if (!tasks.isEmpty() && threads.size() < maxPoolSize){
            PoolWorker poolWorker = new PoolWorker();
            poolWorker.start();
            threads.add(poolWorker);
        }
        tasks.add(runnable);
    }

    class PoolWorker extends Thread {
        public void run() {
            Runnable task;
            while (true) {
                synchronized (tasks) {
                    if (tasks.isEmpty() && threads.size() > minPoolSize) {
                        threads.remove(this);
                        return;
                    }
                    task = tasks.remove();
                }
                task.run();
            }

        }
    }
}
