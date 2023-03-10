/*
 * This Java source file was generated by the Gradle 'init' task.
 *
 */
package cs;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.net.URL;
import java.net.URLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;

public class App {

    static String getURL(@NonNull String target)
    throws IOException {
        StringBuilder content = new StringBuilder();
        URL url = new URL(target);
        URLConnection urlConnection = url.openConnection();
        try (
            Reader ir = new InputStreamReader(urlConnection.getInputStream());
            BufferedReader br = new BufferedReader(ir);
        ) {
            br.lines().forEach(line ->
                {
                    content.append(line)
                           .append('\n');
                });
        }
        return content.toString();
    }

    static void runSimpleThreads() {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, 100_000).forEach(i -> executor.submit(() -> {
                Thread.sleep(Duration.ofSeconds(1).getSeconds());
                System.out.println(i);
                return i;
            }));
        }
    }

    static void runUrlThreads() {
        System.out.println("HERE");
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, 1_000).forEach(i -> executor.submit(() -> {
                try {
                    System.out.println(
                        App.getURL("http://localhost:8080/hello")
                    );
                }
                catch ( IOException iox ) {
                    System.out.println(iox.getMessage());
                }
                return i;
            }));
        }
    }

    static void runVirtualThreads() {
        var counter = new AtomicInteger();
        IntStream.range(0, 1_000).forEachOrdered(
            n -> {
                Thread.startVirtualThread(
                    () -> {
                        int count = counter.incrementAndGet();
                        try {
                            Thread.sleep(Duration.ofSeconds(1).getSeconds());
                        }
                        catch ( InterruptedException ie ) {
                            System.out.println(ie.getMessage());
                        }
                    });
                }
        );
    }

    static void runOsThreads() {
        var counter = new AtomicInteger();
        IntStream.range(0, 100).forEachOrdered(
            n -> {
                new Thread(
                    () -> {
                        int count = counter.incrementAndGet();
                        try {
// Side effect of blocked sockets
// virtual case is non-blocking
// and need to call join before exit of main().
//                            System.out.println(
//                                    App.getURL("http://localhost:8080/hello")
//                            );
                            Thread.sleep(Duration.ofSeconds(1).getSeconds());
                        } catch (InterruptedException ie) {
                            System.out.println(ie.getMessage());
                        }
                    }
                ).start();
            });
        System.out.println("Number threads created is " + counter);
    }

    public static void main(String[] args) {
        App.runSimpleThreads();
        App.runUrlThreads();
        App.runVirtualThreads();
        App.runOsThreads();
    }
}

