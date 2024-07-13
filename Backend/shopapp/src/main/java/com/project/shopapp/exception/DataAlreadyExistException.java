package com.project.shopapp.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class DataAlreadyExistException extends DataIntegrityViolationException {
    public DataAlreadyExistException(String msg) {
        super(msg);
    }
}
