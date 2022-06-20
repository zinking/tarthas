package tdemos;

import tbytekit.ByteKit;
import java.util.concurrent.TimeUnit;

public class ByteKitDemo {
    public static class Sample {
        private int exceptionCount = 0;
        public String hello(String str, boolean exception) {
            if (exception) {
                exceptionCount++;
                throw new RuntimeException("test exception, str: " + str);
            }
            return "hello " + str;
        }
    }

    public static void main(String[] args) throws Exception {
        final Sample sample = new Sample();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; ++i) {
                    try {
                        TimeUnit.SECONDS.sleep(3);
                        String result = sample.hello("" + i, (i % 3) == 0);
                        System.out.println("call hello result: " + result);
                    } catch (Throwable e) {
                        System.out.println("call hello exception: " + e.getMessage());
                    }
                }
            }
        });
        t.start();
        ByteKit.inhanceWatch(Sample.class, "hello");
        System.in.read();
    }

}

