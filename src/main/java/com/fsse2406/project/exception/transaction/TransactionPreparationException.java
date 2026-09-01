package com.fsse2406.project.exception.transaction;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TransactionPreparationException extends RuntimeException {
    public TransactionPreparationException(String message) {
        super(message);
    }
}
