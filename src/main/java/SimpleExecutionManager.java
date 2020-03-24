import java.util.ArrayDeque;

public class SimpleExecutionManager implements ExecutionManage {
    private volatile boolean isInterrupt = false;
    private volatile boolean isFinished = false;
    private volatile int completedTasks = 0;
    private volatile int failedTasks = 0;
    private volatile int interruptTasks = 0;
    private final Object locker = new Object();

    public Context execute(Runnable callback, Runnable... tasks) {
        Thread mainThread = new Thread(() -> {
            ArrayDeque<Thread> threads = new ArrayDeque<>();
            for (Runnable task : tasks) {
                synchronized (locker) {
                    if (!isInterrupt) {
                        try {
                            Thread thread = new Thread(task);
                            threads.add(thread);
                            thread.start();
                            completedTasks++;
                        } catch (RuntimeException e) {
                            failedTasks++;
                        }
                    } else {
                        interruptTasks++;
                    }
                }
            }
            for (Thread thread: threads) {
                try {
                    thread.join();
                } catch (InterruptedException ignored) {
                }
            }
            callback.run();
            isFinished = true;
        });
        mainThread.start();
        return new SimpleContext();
    }

    public class SimpleContext implements Context {
        public int getCompletedTaskCount() {
            synchronized (locker) {
                return completedTasks;
            }
        }

        public int getFailedTaskCount() {
            synchronized (locker) {
                return failedTasks;
            }
        }

        public int getInterruptedTaskCount() {
            synchronized (locker) {
                return interruptTasks;
            }
        }

        public void interrupt() {
            synchronized (locker) {
                isInterrupt = true;
            }
        }

        public boolean isFinished() {
            synchronized (locker) {
                return isFinished;
            }
        }
    }
}

