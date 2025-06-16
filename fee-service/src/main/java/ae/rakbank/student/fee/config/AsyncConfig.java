package ae.rakbank.student.fee.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean("feeTaskExecutor")
    public Executor feeTaskExecutor() {
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setCorePoolSize(50);
        exec.setMaxPoolSize(200);
        exec.setQueueCapacity(500);
        exec.setThreadNamePrefix("fee-exec-");
        exec.initialize();
        return exec;
    }
}
