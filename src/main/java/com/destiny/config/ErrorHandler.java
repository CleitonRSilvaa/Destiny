package com.destiny.config;

import com.destiny.model.MensagemResponse;
import com.destiny.model.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<MensagemResponse> handleValidationException(ValidationException ex) {
        MensagemResponse error = new MensagemResponse();
        error.setMessage(ex.getMessage());
        error.setDetails(ex.getErrors());
        error.setStatus(400);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<MensagemResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        MensagemResponse error = new MensagemResponse();
        List<String> detalhes = new ArrayList<>();
        detalhes.add("O corpo da solicitação contém valores inválidos. Por favor, verifique e tente novamente.");
        detalhes.add(ex.getMessage());
        error.setMessage("corpo da solicitação invalido");
        error.setDetails(detalhes);
        error.setStatus(400);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);

    }
}
