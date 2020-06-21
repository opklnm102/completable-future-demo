import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 동시에 n개의 request 후 하나라도 완료시 callback 진행
 * `CompletableFuture.anyOf()` 사용
 * thenAcceptAsync에서 callback 처리
 */
public class Case2 {

    public void doTask() throws Exception {

        var task1 = CompletableFuture.supplyAsync(() -> buildMessage(1));
        var task2 = CompletableFuture.supplyAsync(() -> buildMessage(2));
        var task3 = CompletableFuture.supplyAsync(() -> buildMessage(3));

        var tasks = Arrays.asList(task1, task2, task3);
        CompletableFuture.anyOf(tasks.toArray(new CompletableFuture[3]))
                         .thenAcceptAsync(message -> System.out.println(Thread.currentThread().getName() + " " + message));

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
