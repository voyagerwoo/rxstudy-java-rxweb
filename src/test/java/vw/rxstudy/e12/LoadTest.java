package vw.rxstudy.e12;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@Slf4j
public class LoadTest {
    @Test
    public void main() throws BrokenBarrierException, InterruptedException {
        double total = loadTest("http://localhost:32783/rest?idx={idx}");
        assertThat(total, is(lessThan(5.0d)));
    }

    private double loadTest(String url) throws InterruptedException, BrokenBarrierException {
        AtomicInteger counter = new AtomicInteger(0);
        ExecutorService es = Executors.newFixedThreadPool(100);
//        CyclicBarrier barrier = new CyclicBarrier(101);
        RestTemplate rt = new RestTemplate();

        for (int i = 0; i < 100; i++) {
            es.submit(() -> {
                int idx = counter.addAndGet( 1);
//                barrier.await();

                log.info("Thredad {}", idx);

                StopWatch sw = new StopWatch();
                sw.start();

                String res = rt.getForObject(url, String.class, idx);

                sw.stop();
                log.info("Elapsed : {}, {}, {}", idx, sw.getTotalTimeSeconds(), res);
                return null;
            });
        }

//        barrier.await();
        StopWatch main = new StopWatch();
        main.start();

        es.shutdown();
        es.awaitTermination(100, TimeUnit.SECONDS);

        main.stop();
        log.info("Total : {}", main.getTotalTimeSeconds());

        return main.getTotalTimeSeconds();
    }


}
