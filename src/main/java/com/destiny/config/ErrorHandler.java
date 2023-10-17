package com.destiny.config;

import com.destiny.model.MensagemResponse;
import com.destiny.model.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

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

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<MensagemResponse> handleDisabledException(DisabledException ex) {

        MensagemResponse error = new MensagemResponse();
        error.setMessage("Conta de usuário inativa");
        List<String> detalhes = new ArrayList<>();
        detalhes.add(ex.getMessage());
        error.setDetails(detalhes);
        error.setStatus(401); // Unauthorized
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<MensagemResponse> handleInternalAuthenticationException(
            InternalAuthenticationServiceException ex) {
        System.out.println("esta no erroHandler ex:" + ex.getMessage());
        MensagemResponse error = new MensagemResponse();
        error.setMessage("Conta de usuário inativa");
        List<String> detalhes = new ArrayList<>();
        detalhes.add(ex.getMessage());
        error.setDetails(detalhes);
        error.setStatus(401); // Unauthorized
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<MensagemResponse> handleBadCredentialsException(BadCredentialsException ex) {
        System.out.println("esta no erroHandler ex:" + ex.getMessage());
        MensagemResponse error = new MensagemResponse();
        error.setMessage("Email ou senha incorretos");
        List<String> detalhes = new ArrayList<>();
        detalhes.add(ex.getMessage());
        error.setDetails(detalhes);
        error.setStatus(401); // Unauthorized
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handleNotFoundError() {
        ModelAndView mav = new ModelAndView("error404"); // nome do arquivo HTML sem a extensão
        return mav;
    }

}
