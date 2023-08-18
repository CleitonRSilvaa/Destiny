package com.pi4.pi4.controller;

import com.pi4.pi4.model.MensagemResponse;
import com.pi4.pi4.model.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ErrorHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<MensagemResponse> handleValidationException(ValidationException ex) {
        MensagemResponse error = new MensagemResponse();
        error.setMessage(ex.getMessage());
        error.setDetails(ex.getErrors());
        error.setStatus(400);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
