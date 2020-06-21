package me.opklnm102.spring;


import org.junit.jupiter.api.Test;
import org.springframework.util.concurrent.ListenableFutureTask;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ListenableTest {

    private final Runnable task = () -> {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(Thread.currentThread().getName() + " task done");
    };

    @Test
    public void doTask() {
        System.out.println(Thread.currentThread().getName() + " completableFuture");
        completableFuture();

        System.out.println("\n" + Thread.currentThread().getName() + " listenableFuture");
        listenableFuture();

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void completableFuture() {
        CompletableFuture.runAsync(task)
                         .thenCompose(aVoid -> CompletableFuture.runAsync(task))
                         .thenAcceptAsync(aVoid -> System.out.println(Thread.currentThread().getName() + " all task completed!!"))
                         .exceptionally(throwable -> {
                             System.out.println(Thread.currentThread().getName() + " exception occurred!!");
                             return null;
                         });
    }

    // callback hell...
    private void listenableFuture() {
        var listenableFutureTask = new ListenableFutureTask(task, "completed!");
        listenableFutureTask.addCallback(result -> {
                    System.out.println(Thread.currentThread().getName() + " all task completed!!");

                    var listenableFutureTask1 = new ListenableFutureTask<>(task, "completed!!");
                    listenableFutureTask1.addCallback(result1 -> System.out.println(Thread.currentThread().getName() + " all task completed!!"),
                            ex -> System.out.println(Thread.currentThread().getName() + " exception occurred!!"));
                    listenableFutureTask1.run();
                },
                ex -> System.out.println(Thread.currentThread().getName() + " exception occurred!!"));
        listenableFutureTask.run();
    }
}
