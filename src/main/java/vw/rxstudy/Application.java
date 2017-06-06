package vw.rxstudy;

import org.springframework.boot.SpringApplication;
import vw.rxstudy.e12.E12Application;
import vw.rxstudy.e12.RemoteService;

public class Application {
    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", "main");
        System.setProperty("reactor.ipc.netty.workerCount", "1");
        System.setProperty("reactor.ipc.netty.pool.nextConnections", "2000");
        SpringApplication.run(E12Application.class, args);

        System.setProperty("spring.profiles.active", "remote");
        System.setProperty("server.port", "7071");
        System.setProperty("server.tomcat.max-threads", "1000");
        SpringApplication.run(RemoteService.class, args);
    }
}
