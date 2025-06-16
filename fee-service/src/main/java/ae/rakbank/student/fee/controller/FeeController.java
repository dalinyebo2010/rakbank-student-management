package ae.rakbank.student.fee.controller;

import ae.rakbank.student.fee.dto.StudentReceiptResponse;
import ae.rakbank.student.fee.model.FeePayment;
import ae.rakbank.student.fee.repository.FeePaymentRepository;
import ae.rakbank.student.fee.StudentClient;
import feign.FeignException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.Collections;
import java.util.concurrent.CompletionException;

@RestController
@RequestMapping("/fees")
@Tag(name = "Fees", description = "Operations related to fee collection")
public class FeeController {

    private final FeePaymentRepository repository;
    private final StudentClient studentClient;
    private final Executor feeTaskExecutor;

    @Autowired
    public FeeController(FeePaymentRepository repository,
                         StudentClient studentClient,
                         @Qualifier("feeTaskExecutor") Executor feeTaskExecutor) {
        this.repository = repository;
        this.studentClient = studentClient;
        this.feeTaskExecutor = feeTaskExecutor;
    }

    @PostMapping("/{studentId}")
    @Operation(summary = "Pay a student fee")
    public CompletableFuture<ResponseEntity<FeePayment>> payFee(
            @PathVariable Long studentId,
            @RequestParam double amount) {
        // Run on custom executor to offload work
        return CompletableFuture.supplyAsync(() -> {
            // Validate student exists (cached)
            fetchStudent(studentId);

            // Create payment
            FeePayment payment = new FeePayment();
            payment.setStudentId(studentId);
            payment.setAmount(amount);
            payment.setPaymentDate(LocalDateTime.now());

            try {
                FeePayment saved = repository.save(payment);
                return ResponseEntity.status(HttpStatus.CREATED).body(saved);
            } catch (Exception ex) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Failed to save fee payment", ex);
            }
        }, feeTaskExecutor);
    }

    @GetMapping("/{studentId}")
    @Operation(summary = "Get student and fee payment receipts")
    public CompletableFuture<ResponseEntity<StudentReceiptResponse>> getReceipts(Long studentId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String student = studentClient.getStudentById(studentId); // may throw FeignException
                List<FeePayment> payments = repository.findByStudentId(studentId);
                StudentReceiptResponse response = new StudentReceiptResponse(student, payments);
                return ResponseEntity.ok(response);
            } catch (FeignException.NotFound e) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student ID " + studentId + " not found");
            } catch (FeignException e) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error fetching student data");
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching data");
            }
        }, feeTaskExecutor);
    }


    /**
     * Fetches student info with caching to reduce remote calls.
     */
    @Cacheable(value = "students", key = "#studentId")
    private Object fetchStudent(Long studentId) {
        try {
            return studentClient.getStudentById(studentId);
        } catch (FeignException.NotFound ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student ID " + studentId + " not found");
        } catch (FeignException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error fetching data");
        }
    }
}
