import java.util.*;

public class FixedThreadPool implements ThreadPool {
    private final ArrayList<PoolWorker> threads;
    private final LinkedList<Runnable> tasks;

    public FixedThreadPool(int poolSize) {
        tasks = new LinkedList<Runnable>();
        threads = new ArrayList<PoolWorker>(poolSize);
    }

    public void start() {
        for (PoolWorker thread : threads) {
            thread.start();
        }
    }

    public void execute(Runnable runnable) {
        synchronized (tasks) {
            tasks.add(runnable);
            notify();
        }
    }

    class PoolWorker extends Thread {
        public void run() {
            Runnable task;
            while (true) {
                synchronized (tasks) {
                    while (tasks.isEmpty()) {
                        try {
                            wait();
                        } catch (InterruptedException ignored) {
                        }
                    }
                    task = tasks.remove();
                }
                task.run();
            }
        }
    }
}
