package aspectum;

public interface Callback {
    void before         (long nanoTime, Thread t, Class type, String methodName, Object[]  args        );
    void afterReturning (long nanoTime, Thread t, Class type, String methodName, Object    returnValue );
    void afterThrowing  (long nanoTime, Thread t, Class type, String methodName, Throwable th          );
}
