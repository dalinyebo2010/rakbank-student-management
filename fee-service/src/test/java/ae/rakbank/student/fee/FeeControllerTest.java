package ae.rakbank.student.fee.controller;

import ae.rakbank.student.fee.dto.StudentReceiptResponse;
import ae.rakbank.student.fee.model.FeePayment;
import ae.rakbank.student.fee.repository.FeePaymentRepository;
import ae.rakbank.student.fee.StudentClient;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletableFuture;

public class FeeControllerTest {

    @Mock
    private FeePaymentRepository repository;

    @Mock
    private StudentClient studentClient;

    private Executor feeTaskExecutor;
    private FeeController feeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        feeTaskExecutor = Runnable::run; // synchronous executor for testing
        feeController = new FeeController(repository, studentClient, feeTaskExecutor);
    }

    @Test
    void payFee_shouldReturnCreated_whenStudentExistsAndSaveSucceeds() throws Exception {
        Long studentId = 1L;
        double amount = 100.0;
        FeePayment savedPayment = new FeePayment();
        savedPayment.setId(1L);
        savedPayment.setStudentId(studentId);
        savedPayment.setAmount(amount);
        savedPayment.setPaymentDate(LocalDateTime.now());

        when(studentClient.getStudentById(studentId)).thenReturn("mock-student");
        when(repository.save(any(FeePayment.class))).thenReturn(savedPayment);

        CompletableFuture<ResponseEntity<FeePayment>> future = feeController.payFee(studentId, amount);
        ResponseEntity<FeePayment> response = future.get();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(savedPayment.getStudentId(), response.getBody().getStudentId());
        assertEquals(savedPayment.getAmount(), response.getBody().getAmount());
    }

    @Test
    void payFee_shouldThrowNotFound_whenStudentDoesNotExist() {
        Long studentId = 99L;
        double amount = 100.0;

        when(studentClient.getStudentById(studentId)).thenThrow(FeignException.NotFound.class);

        CompletableFuture<ResponseEntity<FeePayment>> future = feeController.payFee(studentId, amount);

        CompletionException ex = assertThrows(CompletionException.class, future::join);
        Throwable cause = ex.getCause();
        assertInstanceOf(ResponseStatusException.class, cause);
        assertEquals(HttpStatus.NOT_FOUND, ((ResponseStatusException) cause).getStatusCode());
    }


    @Test
    void payFee_shouldThrowInternalError_whenSaveFails() {
        Long studentId = 1L;
        double amount = 100.0;

        when(studentClient.getStudentById(studentId)).thenReturn("mock-student");
        when(repository.save(any(FeePayment.class))).thenThrow(new RuntimeException("Database error"));

        CompletableFuture<ResponseEntity<FeePayment>> future = feeController.payFee(studentId, amount);

        CompletionException thrown = assertThrows(CompletionException.class, () -> future.join());
        Throwable cause = thrown.getCause();

        assertTrue(cause instanceof ResponseStatusException);
        ResponseStatusException rse = (ResponseStatusException) cause;
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, rse.getStatusCode());
        assertTrue(rse.getReason().contains("Failed to save fee payment"));
    }

    @Test
    void getReceipts_shouldReturnStudentReceiptResponse_whenStudentAndPaymentsExist() throws Exception {
        Long studentId = 1L;
        String studentMock = "{\"id\":1,\"name\":\"John\"}";
        FeePayment payment = new FeePayment();
        payment.setStudentId(studentId);
        payment.setAmount(100.0);
        payment.setPaymentDate(LocalDateTime.now());

        List<FeePayment> payments = Collections.singletonList(payment);

        when(studentClient.getStudentById(studentId)).thenReturn(studentMock);
        when(repository.findByStudentId(studentId)).thenReturn(payments);

        CompletableFuture<ResponseEntity<StudentReceiptResponse>> future = feeController.getReceipts(studentId);
        ResponseEntity<StudentReceiptResponse> response = future.get();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(studentMock, response.getBody().getStudent());
        assertEquals(1, response.getBody().getPayments().size());
        assertSame(payment, response.getBody().getPayments().get(0));
    }


    @Test
    void getReceipts_shouldThrowNotFound_whenStudentDoesNotExist() {
        Long studentId = 2L;

        FeignException notFoundEx = mock(FeignException.NotFound.class);
        when(studentClient.getStudentById(studentId)).thenThrow(notFoundEx);

        CompletableFuture<ResponseEntity<StudentReceiptResponse>> future = feeController.getReceipts(studentId);

        CompletionException thrown = assertThrows(CompletionException.class, future::join);
        Throwable cause = thrown.getCause();

        assertTrue(cause instanceof ResponseStatusException);
        assertEquals(HttpStatus.NOT_FOUND, ((ResponseStatusException) cause).getStatusCode());
    }


    @Test
    void getReceipts_shouldThrowBadGateway_whenFeignFailsUnexpectedly() {
        Long studentId = 3L;
        when(studentClient.getStudentById(studentId)).thenThrow(mock(FeignException.class));

        CompletableFuture<ResponseEntity<StudentReceiptResponse>> future = feeController.getReceipts(studentId);
        try {
            future.join(); // This will throw CompletionException
            fail("Expected ResponseStatusException");
        } catch (CompletionException ex) {
            assertTrue(ex.getCause() instanceof ResponseStatusException);
            ResponseStatusException rse = (ResponseStatusException) ex.getCause();
            assertEquals(HttpStatus.BAD_GATEWAY, rse.getStatusCode());
        }
    }

    @Test
    void getReceipts_shouldThrowInternalServerError_onUnexpectedException() {
        Long studentId = 4L;
        String studentMock = "{\"id\":4,\"name\":\"Jane\"}";
        when(studentClient.getStudentById(studentId)).thenReturn(studentMock);
        when(repository.findByStudentId(studentId)).thenThrow(new RuntimeException("DB failure"));

        CompletableFuture<ResponseEntity<StudentReceiptResponse>> future = feeController.getReceipts(studentId);
        try {
            future.join(); // This throws CompletionException
            fail("Expected ResponseStatusException");
        } catch (CompletionException ex) {
            assertTrue(ex.getCause() instanceof ResponseStatusException);
            ResponseStatusException rse = (ResponseStatusException) ex.getCause();
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, rse.getStatusCode());
            assertEquals("Error fetching data", rse.getReason());
        }
    }
}
