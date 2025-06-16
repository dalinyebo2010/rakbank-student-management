package ae.rakbank.student.management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean("studentTaskExecutor")
    public Executor studentTaskExecutor() {
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setCorePoolSize(50);   // start with 50 threads
        exec.setMaxPoolSize(200);   // scale up to 200
        exec.setQueueCapacity(500); // queue beyond core before spawning extras
        exec.setThreadNamePrefix("student-exec-");
        exec.initialize();
        return exec;
    }
}
