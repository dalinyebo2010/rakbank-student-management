package ae.rakbank.student.management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", unique = true, nullable = false)
    private Long studentId;

    @Column(length = 50)
    @Size(min = 2, max = 50, message = "Student name must be between 2 and 50 characters")
    private String studentName;

    @Column(length = 5)
    @Size(min = 1, max = 5, message = "Grade must be between 1 and 5 characters")
    private String grade;

    @Column(length = 15)
    @Size(min = 10, max = 15, message = "Mobile number must be between 10 and 15 characters")
    private String mobileNumber;

    @Column(length = 100)
    @Size(min = 4, max = 100, message = "School name must be between 4 and 100 characters")
    private String schoolName;
}
