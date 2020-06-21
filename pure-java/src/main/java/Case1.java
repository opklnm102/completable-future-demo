import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 동시에 n개의 request 후 모두 완료시 callback 진행
 * `CompletableFuture.allOf()` 사용
 * thenAcceptAsync에서 모두 모아 처리
 * 선후 관계가 없는 데이터를 동시에 조회할 때, 적절히 사용 가능
 */
public class Case1 {

    public void doTask() throws Exception {
        var task1 = CompletableFuture.supplyAsync(() -> buildMessage(1));
        var task2 = CompletableFuture.supplyAsync(() -> buildMessage(2));
        var task3 = CompletableFuture.supplyAsync(() -> buildMessage(3));

        var tasks = Arrays.asList(task1, task2, task3);
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[3]))
                         .thenApplyAsync(result -> tasks.stream()
                                                        .map(CompletableFuture::join)
                                                        .collect(Collectors.toList()))
                         .thenAcceptAsync(messages -> messages.forEach(message -> System.out.println(Thread.currentThread().getName() + " " + message)));

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String buildMessage(int index) {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return index + " Completed!!";
    }
}
