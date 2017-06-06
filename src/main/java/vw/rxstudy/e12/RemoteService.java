package vw.rxstudy.e12;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@SpringBootApplication
@Slf4j
public class RemoteService {

	/*
	* Spring boot 2.0.0.M1인 상황에서 기본은 netty 서버이다.
	* pom.xml에 embedtomcat 의존성 추가하면 기본은 tomcat 서버이다.
	* 그래도 어떤 서버 어플리케이션을 사용할지 명시하기 위해서 아래 처럼 적어두었다.
	* */
	@Bean
	@Profile("remote")
	TomcatReactiveWebServerFactory tomcatReactiveWebServerFactory() {
		return new TomcatReactiveWebServerFactory();
	}

	@RestController
	static class RemoteController {
		@GetMapping("/service")
		public Mono<String> service(String req) throws Exception {
			Thread.sleep(1000);
			log.info(req);
//			throw new RuntimeException();
			return Mono.just(req + "/service1");
		}

		@GetMapping("/service2")
		public Mono<String> service2(String req) throws Exception {
			log.info(req);
			Thread.sleep(1000);
			return Mono.just(req + "/service2");
		}
	}
}
