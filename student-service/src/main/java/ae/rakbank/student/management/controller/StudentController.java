//package ae.rakbank.student.management.controller;
//
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.http.HttpStatus;
//import org.springframework.beans.factory.annotation.Autowired;
//import ae.rakbank.student.management.model.Student;
//import ae.rakbank.student.management.repository.StudentRepository;
//import jakarta.validation.Valid;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import io.swagger.v3.oas.annotations.Operation;
//import ae.rakbank.student.management.exception.StudentNotFoundException;
//import ae.rakbank.student.management.exception.StudentCreationException;
//
//@RestController
//@RequestMapping("/students")
//@Tag(name = "Students", description = "Operations related to students")
//public class StudentController {
//
//    @Autowired
//    private StudentRepository repository;
//
//    @PostMapping
//    @Operation(summary = "Create a new student")
//    public ResponseEntity<Student> create(@RequestBody @Valid Student student) {
//        try {
//            Student savedStudent = repository.save(student);
//            return new ResponseEntity<>(savedStudent, HttpStatus.CREATED);
//        } catch (Exception ex) {
//            throw new StudentCreationException("Failed to create student", ex);
//        }
//    }
//
//    @GetMapping("/{studentId}")
//    @Operation(summary = "Get student by student ID")
//    public ResponseEntity<Student> getById(@PathVariable Long studentId) {
//        Student student = repository.findByStudentId(studentId)
//                .orElseThrow(() -> new StudentNotFoundException(studentId));
//        return ResponseEntity.ok(student);
//    }
//}

package ae.rakbank.student.management.controller;

import ae.rakbank.student.management.exception.StudentCreationException;
import ae.rakbank.student.management.exception.StudentNotFoundException;
import ae.rakbank.student.management.model.Student;
import ae.rakbank.student.management.repository.StudentRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

@RestController
@RequestMapping("/students")
@Tag(name = "Students", description = "Operations related to students")
public class StudentController {

    private final StudentRepository repository;
    private final Executor studentTaskExecutor;

    @Autowired
    public StudentController(
            StudentRepository repository,
            @Qualifier("studentTaskExecutor") Executor studentTaskExecutor
    ) {
        this.repository = repository;
        this.studentTaskExecutor = studentTaskExecutor;
    }

    @PostMapping
    @Operation(summary = "Create a new student")
    public Callable<ResponseEntity<Student>> create(
            @RequestBody @Valid Student student) {
        return () -> {
            try {
                Student saved = repository.save(student);
                return ResponseEntity.status(201).body(saved);
            } catch (Exception ex) {
                throw new StudentCreationException("Failed to create student", ex);
            }
        };
    }

    @GetMapping("/{studentId}")
    @Operation(summary = "Get student by student ID")
    public Callable<ResponseEntity<Student>> getById(
            @PathVariable Long studentId) {
        return () -> {
            Student student = repository.findByStudentId(studentId)
                    .orElseThrow(() -> new StudentNotFoundException(studentId));
            return ResponseEntity.ok(student);
        };
    }
}
