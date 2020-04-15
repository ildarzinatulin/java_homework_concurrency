import java.util.concurrent.Callable;

public class Task<T> {

    private enum Status {
        Finished, InProcess, DidntStart
    }

    private final Callable<T> task;
    private volatile T result;
    private volatile TaskCallableException exception;
    private volatile Status firstThreadStatus = Status.DidntStart;

    public Task(Callable<T> callable) {
        task = callable;
    }

    public T get() throws Exception {
        Status status;
        synchronized (this) {
            status = firstThreadStatus;
            if (firstThreadStatus == Status.DidntStart) {
                firstThreadStatus = Status.InProcess;
            }
        }

        while (status == Status.InProcess) {
            this.wait();
            status = firstThreadStatus;
        }

        if (status == Status.Finished) {
            if (result != null) {
                return result;
            } else {
                throw exception;
            }
        }

        try {
            result = task.call();
            return result;
        } catch (Exception e) {
            exception = new TaskCallableException("Exception in task");
            throw exception;
        } finally {
            firstThreadStatus = Status.Finished;
            this.notifyAll();
        }
    }
}

