package ae.rakbank.student.fee.dto;

import ae.rakbank.student.fee.model.FeePayment;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
public class StudentReceiptResponse {
    private Object student;
    private List<FeePayment> payments;

    public StudentReceiptResponse(Object student, List<FeePayment> payments) {
        this.student = student;
        this.payments = payments != null ? payments : Collections.emptyList();
    }
}
