package ae.rakbank.student.fee;

import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.cloud.openfeign.EnableFeignClients;

@OpenAPIDefinition(
		info = @Info(
				title = "Fee Management API",
				version = "1.0",
				description = "API for managing students fees"
		)
)
@SpringBootApplication
@EnableFeignClients
public class FeeServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(FeeServiceApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() { return new RestTemplate(); }
}
