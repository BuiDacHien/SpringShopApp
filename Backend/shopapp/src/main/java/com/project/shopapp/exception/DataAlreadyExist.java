package com.project.shopapp.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class DataAlreadyExist extends DataIntegrityViolationException {

    public DataAlreadyExist(String msg) {
        super(msg);
    }

    public DataAlreadyExist(String msg, Throwable cause) {
        super(msg, cause);
    }
}
