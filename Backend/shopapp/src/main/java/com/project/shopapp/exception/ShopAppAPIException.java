package com.project.shopapp.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ShopAppAPIException extends RuntimeException {
    private HttpStatus status;
    private String message;
}
