public interface ExecutionManage {
    Context execute(Runnable callback, Runnable... tasks);
}
