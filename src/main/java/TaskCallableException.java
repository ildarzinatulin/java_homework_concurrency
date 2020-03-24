class TaskCallableException extends RuntimeException {
    TaskCallableException(String runtimeException_in_callable) {
        super(runtimeException_in_callable);
    }
}
