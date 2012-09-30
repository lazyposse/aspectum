package aspectum;

public interface Callback {
	@SuppressWarnings("rawtypes")
	void before(Thread t, Class type, String methodName, Object[] args);

	@SuppressWarnings("rawtypes")
	void afterReturning(Thread t, Class type, String methodName,
			Object returnValue);

	@SuppressWarnings("rawtypes")
	void afterThrowing(Thread t, Class type, String methodName, Throwable th);
}
