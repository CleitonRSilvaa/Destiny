package com.destiny.controller;

import com.destiny.model.MensagemResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class LoginController {


    @PostMapping("login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MensagemResponse> login() {
        MensagemResponse mensagemResponse = new MensagemResponse();
        List<String> detalhes = new ArrayList<>();

        mensagemResponse.setStatus(200);
        mensagemResponse.setMessage("sucess");
        mensagemResponse.setDetails(detalhes);

        return new ResponseEntity<>(mensagemResponse, HttpStatus.OK);
    }


}
