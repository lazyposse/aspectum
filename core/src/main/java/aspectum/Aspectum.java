package aspectum;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

public @Aspect
abstract class Aspectum {
	public @Pointcut
	abstract void aspectum();

	private static Callback cb = new CallbackImpl();

	public static void setCallback(Callback callback) {
		cb = callback;
	}

	@Before(value = "aspectum()", argNames = "joinPoint")
	public void before(JoinPoint joinPoint) {
		if (cb == null) {
			return;
		}
		Signature signature = joinPoint.getSignature();
		cb.before(Thread.currentThread(), signature.getDeclaringType(),
				signature.getName(), joinPoint.getArgs());
	}

	@AfterReturning(pointcut = "aspectum()", returning = "returnValue", argNames = "joinPoint,returnValue")
	public void afterReturning(JoinPoint joinPoint, Object returnValue) {
		if (cb == null) {
			return;
		}
		Signature signature = joinPoint.getSignature();
		cb.afterReturning(Thread.currentThread(), signature.getDeclaringType(),
				signature.getName(), returnValue);
	}

	@AfterThrowing(pointcut = "aspectum()", throwing = "throwable", argNames = "joinPoint,throwable")
	public void afterThrowing(JoinPoint joinPoint, Throwable throwable) {
		if (cb == null) {
			return;
		}
		Signature signature = joinPoint.getSignature();
		cb.afterThrowing(Thread.currentThread(), signature.getDeclaringType(),
				signature.getName(), throwable);
	}
}