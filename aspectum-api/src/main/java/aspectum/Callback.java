package aspectum;

public interface Callback {
    void before         (long nanoTime, Thread t, Class type, String methodName, Object[]  args        ) throws Exception;
    void afterReturning (long nanoTime, Thread t, Class type, String methodName, Object    returnValue ) throws Exception;
    void afterThrowing  (long nanoTime, Thread t, Class type, String methodName, Throwable th          ) throws Exception;
}
