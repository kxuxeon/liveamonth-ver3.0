package teamproject.lam_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import teamproject.lam_server.config.AopConfig;
import teamproject.lam_server.trace.logtrace.LogTrace;
import teamproject.lam_server.trace.logtrace.ThreadLocalLogTrace;

@SpringBootApplication
@Import(AopConfig.class)
public class LamServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(LamServerApplication.class, args);
    }

    @Bean
    public LogTrace logTrace(){
        return new ThreadLocalLogTrace();
    }

}
