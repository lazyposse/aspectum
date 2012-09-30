package aspectum;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

public @Aspect abstract class Aspectum {
    public @Pointcut abstract void aspectum();

    private static Callback cb;

    public static void setCallback(Callback callback) {
        cb = callback;
    }

    public Aspectum() throws Exception {
        String cbNameKey = "aspectum.callback";
        String cbName    = System.getProperty(cbNameKey);

        if (cbName == null) {
            System.err.println();
            System.err.println("+------------------------------------------------------------------------------ ");
            System.err.println("|                                                                               ");
            System.err.println("|                             *** SEVERE ! ***                                  ");
            System.err.println("|                                                                               ");
            System.err.println("|    You must define a Java system property with:                               ");
            System.err.println("|        - key   = " + cbNameKey +                                             "");
            System.err.println("|        - value = <an-aspectum.Callback-implementation>                        ");
            System.err.println("|                                                                               ");
            System.err.println("|    For example:                                                               ");
            System.err.println("|        java -D" + cbNameKey + "=<myAspectumCallbackImpl> ...                  ");
            System.err.println("|                                                                               ");
            System.err.println("|    For more details see:                                                      ");
            System.err.println("|        https://github.com/lazyposse/aspectum                                  ");
            System.err.println("|                                                                               ");
            System.err.println("+------------------------------------------------------------------------------ ");
            System.err.println();
            throw new RuntimeException("Missing Java system property: " + cbNameKey);
        }

        cb = (Callback) Class.forName(cbName).newInstance();
    }

    @Before(value = "aspectum()", argNames = "joinPoint")
    public void before(JoinPoint joinPoint) {
        if (cb == null) {
            return;
        }
        Signature s = joinPoint.getSignature();
        cb.before(Thread.currentThread(), s.getDeclaringType(), s.getName(), joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "aspectum()", returning = "ret", argNames = "joinPoint,ret")
    public void afterReturning(JoinPoint joinPoint, Object ret) {
        if (cb == null) {
            return;
        }
        Signature s = joinPoint.getSignature();
        cb.afterReturning(Thread.currentThread(), s.getDeclaringType(), s.getName(), ret);
    }

    @AfterThrowing(pointcut = "aspectum()", throwing = "t", argNames = "joinPoint,t")
    public void afterThrowing(JoinPoint joinPoint, Throwable t) {
        if (cb == null) {
            return;
        }
        Signature s = joinPoint.getSignature();
        cb.afterThrowing(Thread.currentThread(), s.getDeclaringType(), s.getName(), t);
    }
}
