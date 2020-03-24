import java.util.concurrent.Callable;

public class Task<T> {
    private final Callable<T> task;
    private T result;
    private TaskCallableException exception;

    public Task(Callable<T> callable) {
        task = callable;
    }

    public T get() throws Exception {
        if (result != null) {
            return result;
        }
        if (exception != null) {
            throw exception;
        }
        synchronized (this) {
            try {
                result = task.call();
            } catch (RuntimeException e) {
                exception = new TaskCallableException("RuntimeException in Callable");
                throw exception;
            }
            return result;
        }
    }
}

