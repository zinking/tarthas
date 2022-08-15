package tdemos;

import arthas.ArthasSession;

public class ArthasDemo {
    public static void main(String[] args) throws Throwable {
        ArthasSession.initStub();
        ArthasSession session = ArthasSession.getSession();

        final Sample sample = new Sample();
        session.makeAsyncCommand("watch tdemos.Sample hello -x 2");
        session.pullResults();
        Thread.sleep(1000);
        sample.hello("hello", false);
        session.pullResults();
        session.interrupt();
        session.close();
    }

}

class Sample {
    private int exceptionCount = 0;
    public String hello(String str, boolean exception) {
        if (exception) {
            exceptionCount++;
            throw new RuntimeException("test exception, str: " + str);
        }
        return "hello " + str;
    }
}

