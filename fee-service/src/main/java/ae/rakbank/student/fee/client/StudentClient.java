package ae.rakbank.student.fee;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "student-service", url = "http://localhost:8080/api")
public interface StudentClient {

    @GetMapping("/students/{studentId}")
    String getStudentById(@PathVariable("studentId") Long id);
}
