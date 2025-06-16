//package ae.rakbank.student.management.controller;
//
//import ae.rakbank.student.management.exception.StudentCreationException;
//import ae.rakbank.student.management.model.Student;
//import ae.rakbank.student.management.repository.StudentRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class StudentControllerTest {
//
//    @Mock
//    private StudentRepository studentRepository;
//
//    @InjectMocks
//    private StudentController studentController;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void createStudent_success() {
//        // Arrange
//        Student student = new Student();
//        student.setStudentId(12345L);
//        student.setStudentName("Dali Bhele");
//        student.setGrade("9E");
//        student.setMobileNumber("098765432`1");
//        student.setSchoolName("Reddforrd");
//
//        when(studentRepository.save(student)).thenReturn(student);
//
//        // Act
//        ResponseEntity<Student> response = studentController.create(student);
//
//        // Assert
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertEquals(student, response.getBody());
//        verify(studentRepository, times(1)).save(student);
//    }
//
//    @Test
//    void createStudent_failure_throwsException() {
//        // Arrange
//        Student student = new Student();
//        student.setStudentId(456L);
//
//        when(studentRepository.save(student)).thenThrow(new RuntimeException("DB error"));
//
//        // Act & Assert
//        assertThrows(StudentCreationException.class, () -> {
//            studentController.create(student);
//        });
//
//        verify(studentRepository, times(1)).save(student);
//    }
//}

package ae.rakbank.student.management.controller;

import ae.rakbank.student.management.exception.StudentCreationException;
import ae.rakbank.student.management.exception.StudentNotFoundException;
import ae.rakbank.student.management.model.Student;
import ae.rakbank.student.management.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StudentControllerTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentController studentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createStudent_success() throws Exception {
        // Arrange
        Student student = new Student();
        student.setStudentId(12345L);
        student.setStudentName("Dali Bhele");
        student.setGrade("9E");
        student.setMobileNumber("0987654321");
        student.setSchoolName("Reddforrd");

        when(studentRepository.save(student)).thenReturn(student);

        // Act
        Callable<ResponseEntity<Student>> task = studentController.create(student);
        ResponseEntity<Student> response = task.call();

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(student, response.getBody());
        verify(studentRepository, times(1)).save(student);
    }

    @Test
    void createStudent_failure_throwsException() {
        // Arrange
        Student student = new Student();
        student.setStudentId(456L);

        when(studentRepository.save(student)).thenThrow(new RuntimeException("DB error"));

        // Act
        Callable<ResponseEntity<Student>> task = studentController.create(student);

        // Assert
        StudentCreationException ex = assertThrows(
                StudentCreationException.class,
                () -> task.call()
        );
        verify(studentRepository, times(1)).save(student);
    }

    @Test
    void getById_success() throws Exception {
        // Arrange
        Long id = 1L;
        Student student = new Student();
        student.setStudentId(id);
        student.setStudentName("Alice");
        when(studentRepository.findByStudentId(id)).thenReturn(Optional.of(student));

        // Act
        Callable<ResponseEntity<Student>> task = studentController.getById(id);
        ResponseEntity<Student> response = task.call();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(student, response.getBody());
        verify(studentRepository, times(1)).findByStudentId(id);
    }

    @Test
    void getById_notFound_throwsException() {
        // Arrange
        Long id = 2L;
        when(studentRepository.findByStudentId(id)).thenReturn(Optional.empty());

        // Act
        Callable<ResponseEntity<Student>> task = studentController.getById(id);

        // Assert
        StudentNotFoundException ex = assertThrows(
                StudentNotFoundException.class,
                () -> task.call()
        );
        assertTrue(ex.getMessage().contains(String.valueOf(id)));
        verify(studentRepository, times(1)).findByStudentId(id);
    }
}
