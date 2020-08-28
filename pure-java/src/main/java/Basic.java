import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Basic {

    public void doTask() throws Exception {

//        System.out.println("runAsync");
//        runAsync();
//
//        System.out.println("\nsupplyAsync");
//        supplyAsync();
//
//        System.out.println("\nthenAccept");
//        thenAccept();
//
//        System.out.println("\nthenApply");
//        thenApply();
//
//        System.out.println("\nthenCompose");
//        thenCompose();
//
//        System.out.println("\nthenCombine");
//        thenCombine();
//
//        System.out.println("\nallOf");
//        allOf();
//
//        System.out.println("\nanyOf");
//        anyOf();

        System.out.println("\nexceptionally");
        exceptionally();
    }

    // Runnable Functional Interface, parameter X, return X
    private void runAsync() throws Exception {

        CompletableFuture.runAsync(() -> System.out.println(Thread.currentThread().getName() + " runAsync"))
                         .get();
    }

    // Supplier Functional Interface, parameter X, return O
    private void supplyAsync() throws Exception {

        String result = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " supplyAsync");
            return "ok";
        })
                                         .get();

        System.out.println(Thread.currentThread().getName() + " supplyAsync, result : " + result);
    }

    // Consumer Functional Interface, parameter O, return X
    private void thenAccept() throws Exception {

        CompletableFuture.runAsync(() -> System.out.println(Thread.currentThread().getName() + " runAsync"))
                         .thenAccept(aVoid -> System.out.println(Thread.currentThread().getName() + " " + aVoid));

        CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " supplyAsync");
            return "ok";
        })
                         .thenAccept(s -> System.out.println(Thread.currentThread().getName() + " s"));
    }

    // Function Functional Interface, parameter O, return O
    // ListableFuture의 callback과 동일
    private void thenApply() throws Exception {

        CompletableFuture.runAsync(() -> System.out.println(Thread.currentThread().getName() + " runAsync"))
                         .thenApply(aVoid -> {
                             System.out.println(Thread.currentThread().getName() + " thenApply");
                             return "ok2";
                         })
                         .thenAccept(s -> System.out.println(Thread.currentThread().getName() + " " + s));
    }

    // 순차적으로 실행, Parallel X, 하나의 thread pool
    // return을 다음 처리의 parameter로 사용
    private void thenCompose() throws Exception {

        CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " supplyAsync");
            return "ok";
        })
                         .thenCompose(s -> CompletableFuture.completedFuture(s + " 2"))
                         .thenCompose(s -> CompletableFuture.completedFuture(s + " 3"))
                         .thenAccept(s -> System.out.println(Thread.currentThread().getName() + " complete " + s));
    }

    // Parallel O, 별도의 thread pool
    // return을 조합하여 처리
    private void thenCombine() throws Exception {

        var secondCompletableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(Thread.currentThread().getName() + " supplyAsync - 2");
            return "second";
        });

        CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " supplyAsync - 1");
            return "first";
        })
                         .thenCombine(secondCompletableFuture, (s1, s2) -> s1 + " and " + s2)
                         .thenAccept(s -> System.out.println(Thread.currentThread().getName() + " complete " + s));

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void allOf() throws Exception {

        var completableFuture1 = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " supplyAsync - 1");
            return "1";
        });

        var completableFuture2 = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " supplyAsync - 2");
            return "2";
        });

        var completableFuture3 = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " supplyAsync - 3");
            return "3";
        });

        var futures = Arrays.asList(completableFuture1, completableFuture2, completableFuture3);

        CompletableFuture.allOf(completableFuture1, completableFuture2, completableFuture3)
                         .thenAccept(s -> {
                             System.out.println(Thread.currentThread().getName() + " allOf " + s);

                             var results = futures.stream()
                                                  .map(CompletableFuture::join)
                                                  .collect(Collectors.toList());

                             System.out.println(Thread.currentThread().getName() + " allOf " + results.toString());
                         });
    }

    private void anyOf() throws Exception {

        var completableFuture1 = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " supplyAsync - 1");
            return "1";
        });

        var completableFuture2 = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " supplyAsync - 2");
            return "2";
        });

        var completableFuture3 = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " supplyAsync - 3");
            return "3";
        });

        var futures = Arrays.asList(completableFuture1, completableFuture2, completableFuture3);

        CompletableFuture.anyOf(completableFuture1, completableFuture2, completableFuture3)
                         .thenAccept(s -> {
                             System.out.println(Thread.currentThread().getName() + " anyOf " + s);

                             var results = futures.stream()
                                                  .map(CompletableFuture::join)
                                                  .collect(Collectors.toList());

                             System.out.println(Thread.currentThread().getName() + " anyOf " + results.toString());
                         });
    }

    // Throwable 처리
    // ListableFuture의 Callback.onFailure
    // ListableFuture와 다르게 통합적으로 처리
    private void exceptionally() {

        CompletableFuture.runAsync(() -> System.out.println(Thread.currentThread().getName() + " runAsync"))
                         .thenApply(aVoid -> {
                             System.out.println(Thread.currentThread().getName() + " thenApply");
                             throw new RuntimeException("error!");
                         })
                         .exceptionally(throwable -> {
                             System.out.println(Thread.currentThread().getName() + " " + throwable);
                             return "failed";
                         })
                         .thenAccept(s -> System.out.println(Thread.currentThread().getName() + " " + s));
    }
}
