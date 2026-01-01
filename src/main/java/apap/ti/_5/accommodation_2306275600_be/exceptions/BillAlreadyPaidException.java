package apap.ti._5.accommodation_2306275600_be.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BillAlreadyPaidException extends RuntimeException {
    public BillAlreadyPaidException(String message) {
        super(message);
    }
}
