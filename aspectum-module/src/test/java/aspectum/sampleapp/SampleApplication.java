package aspectum.sampleapp;


public class SampleApplication {

    public static void main(String[] args) {
        try {
            someMethod1();
            someMethod2();
        } catch (Exception e) {
            prn("An exception was thrown");
        }
    }

    static void someMethod1() {
        someMethod1a();
        someMethod1b();
        someMethod1c();
    }

    static void someMethod2() {
        someMethod2a();
    }

    static void someMethod2a() {
        willThrow();
    }

    static void willThrow() {
        throw new RuntimeException();
    }

    static void someMethod1a() { prn("1a"); }

    static void someMethod1b() { prn("1b"); }

    static void someMethod1c() { prn("1c"); }

    static void prn(String msg) {
        System.out.println("SampleApplication> someMethod" + msg);
    }
}
