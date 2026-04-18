package com.linhdv.efms_common_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateLinkException extends RuntimeException {

    public DuplicateLinkException(String message) {
        super(message);
    }
}
