package aspectum.module;

import java.util.concurrent.ConcurrentHashMap;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;

import java.text.SimpleDateFormat;
import java.util.Date;

import aspectum.Callback;

/**
 *
 * A simple callback impl that trace the time of the method call and
 * dump them in a file.
 *
 */
public class MethodTimeCallback implements Callback {

    private final ConcurrentHashMap<Long, BufferedWriter> threadsToWriters = new ConcurrentHashMap<Long, BufferedWriter>();

    private final String sessionDir = System.getProperty("user.home") + "/.am-perf/" + (new SimpleDateFormat("yyyyMMdd-kk'h'mm'm'ss's'").format(new Date()));

    public MethodTimeCallback() throws Exception {
        createSessionDir();
        closeOnShutdown();
    }

    private final void createSessionDir() throws Exception {
        (new File(sessionDir)).mkdirs();
    }

    private final void closeOnShutdown() throws Exception {
        // To avoid losing unflushed data
        Runtime.getRuntime().addShutdownHook(new Thread() { public void run() {
            for (BufferedWriter w : threadsToWriters.values()) {
                closeWriter(w);
            }
        }});
    }

    private final void closeWriter(BufferedWriter w) {
            try {
                w.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
    }

    private final BufferedWriter getWriter(long threadId) throws Exception {
        BufferedWriter w = new BufferedWriter(new FileWriter(sessionDir + "/thread-" + threadId + ".csv"));
        writeHeader(w);
        return w;
    }

    private final void writeHeader(BufferedWriter w) throws Exception {
        w.write("(B)efore/after(R)eturning/after(T)hrowing," +
                "class,"                                     +
                "method,"                                    +
                "before recording nanoTime,"                 +
                "after recording nanoTime\n"                 );
    }

    private final BufferedWriter getWriter() throws Exception {
        final Long tid = Long.valueOf(Thread.currentThread().getId());
        BufferedWriter w = threadsToWriters.get(tid);
        if (w == null) {
            w = getWriter(tid);
            threadsToWriters.put(tid, w);
        }
        return w;
    }

    private final void write(char kind, long nanoTime, Thread t, Class type, String methodName) throws Exception {
        final BufferedWriter o = getWriter();

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
        write('B', nanoTime, t, type, methodName);
    }

    @Override
    public void afterReturning (long nanoTime, Thread t, Class type, String methodName, Object returnValue) throws Exception {
        write('R', nanoTime, t, type, methodName);
    }

    @Override
    public void afterThrowing  (long nanoTime, Thread t, Class type, String methodName, Throwable th) throws Exception {
        write('T', nanoTime, t, type, methodName);
    }
}
