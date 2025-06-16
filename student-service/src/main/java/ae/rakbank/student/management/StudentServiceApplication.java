package ae.rakbank.student.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
		info = @Info(
				title = "Student Management API",
				version = "1.0",
				description = "API for managing students"
		)
)
@SpringBootApplication
public class StudentServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(StudentServiceApplication.class, args);
	}
}
