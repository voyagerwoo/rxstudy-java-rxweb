package vw.rxstudy.e12;

import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@SpringBootApplication
@EnableAsync
@Slf4j
public class E12Application {
	@RestController
	@Profile("main")
	static class Controller {
		static final String URL1 = "http://localhost:7071/service?req={req}";
		static final String URL2 = "http://localhost:7071/service2?req={req}";

		@Autowired
        MyService myService;

		WebClient webClient = WebClient.create();

		@GetMapping("/rest")
		public Mono<String> rest(int  idx) throws Exception {
			/* Reactor는 subscribe를 하지 않으면 API를 호출하지 않는다. */
			/* Controller에서 Mono를 반환하면 spring에서 알아서 subscribe한다. */

//			Mono<ClientResponse> res = webClient.get().uri(URL1, idx).exchange();
//			Mono<Mono<String>> map = res.map(clientResponse -> clientResponse.bodyToMono(String.class));
			log.info("start {}", idx);

			return webClient.get().uri(URL1, idx).exchange()
					.flatMap(c -> c.bodyToMono(String.class))
					.doOnNext(log::info)
					.flatMap(res -> webClient.get().uri(URL2, res).exchange())
					.flatMap(c -> c.bodyToMono(String.class))
					.doOnNext(log::info)
					.flatMap(res -> Mono.fromCompletionStage(myService.work(res)))
					.doOnNext(log::info);
		}
	}

	@Bean
	@Profile("main")
	NettyReactiveWebServerFactory nettyReactiveWebServerFactory() {
		return new NettyReactiveWebServerFactory();
	}

	@Service
	@Profile("main")
	static class MyService {
		@Async
		public CompletableFuture<String> work(String req) {
			return CompletableFuture.completedFuture(req + "/asyncwork");
		}
	}

	public static void main(String[] args) {
		System.setProperty("spring.profiles.active", "main");
		System.setProperty("reactor.ipc.netty.workerCount", "2");
		System.setProperty("reactor.ipc.netty.pool.nextConnections", "2000");
		SpringApplication.run(E12Application.class, args);
	}
}
