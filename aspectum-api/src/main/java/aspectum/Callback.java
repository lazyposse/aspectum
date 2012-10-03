package aspectum;

public interface Callback {
    void before         (Thread t, Class type, String methodName, Object[]  args        );
    void afterReturning (Thread t, Class type, String methodName, Object    returnValue );
    void afterThrowing  (Thread t, Class type, String methodName, Throwable th          );
}
