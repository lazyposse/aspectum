package aspectum.module;

import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import aspectum.Callback;

/**
 *
 * A simple callback impl that trace the time of the method call and
 * dump them in a file.
 *
 */
public class MethodTimeCallback implements Callback {

    BufferedWriter o;

    public MethodTimeCallback() throws Exception {
        String s           = System.getProperty("user.home") + "/measure.tracetime";
        FileWriter fstream = new FileWriter(s);
        o                  = new BufferedWriter(fstream);

        writeHeader();
        closeOnShutdown();
    }

    private final void writeHeader() throws Exception {
        o.write("(B)efore/after(R)eturning/after(T)hrowing," +
                "class,"                                     +
                "method,"                                    +
                "before recording nanoTime,"                 +
                "after recording nanoTime\n"               );
    }

    void closeOnShutdown() throws Exception {
        // To avoid losing unflushed data
        Runtime.getRuntime().addShutdownHook(new Thread() { public void run() {
            try {
                o.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }});
    }

    private final Writer getWriter() {
        return this.o;
    }

    private final void write(char kind, long nanoTime, Thread t, Class type, String methodname) {
        final Writer o = getWriter();

        o.write(kind);
        o.write(',');
        o.write(type.getName());
        o.write(',');
        o.write(methodName);
        o.write(',');
        o.write(Long.toString(nanoTime));
        o.write(',');
        o.write(Long.toString(System.nanoTime()));
        o.write('\n');
    }

    @Override
    public void before         (long nanoTime, Thread t, Class type, String methodName, Object[] args) throws Exception {
        write('B');
    }

    @Override
    public void afterReturning (long nanoTime, Thread t, Class type, String methodName, Object returnValue) throws Exception {
        write('R');
    }

    @Override
    public void afterThrowing  (long nanoTime, Thread t, Class type, String methodName, Throwable th) throws Exception {
        write('T');
    }
}
