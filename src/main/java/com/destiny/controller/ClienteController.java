package com.destiny.controller;


import com.destiny.model.Cliente;
import com.destiny.model.MensagemResponse;
import com.destiny.model.ValidationException;
import com.destiny.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("cliente")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @RequestMapping("clienteList")
    @GetMapping
    public List<Map<String, Object>> list(){
        return clienteRepository.findAllCustom();
    }


    @RequestMapping("/listDetalhada")
    @GetMapping
    public List<Cliente> listAllDetalhes(){
        return clienteRepository.findAll();
    }

    @RequestMapping("add")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MensagemResponse> insertCliente(@RequestBody Cliente cliente) {
        List<String> errors = new ArrayList<>();
        MensagemResponse mensagemResponse = new MensagemResponse();
        List<String> detalhes = new ArrayList<>();

        cliente.setStatusConta((byte) 1);

        if (cliente.getNome() == null) {
            errors.add("nome é obrigatório.");
        }
        if (cliente.getEmail() == null) {
            errors.add("email é obrigatório.");
        }
        if (cliente.getCpf() == null) {
            errors.add("cpf é obrigatório.");
        }
        if (cliente.getSenha() == null) {
            errors.add("senha é obrigatório.");
        }


        if (clienteRepository.findByEmail(cliente.getEmail()) != null) {
            errors.add("email já está cadastrado.");
        }

        if (clienteRepository.findByCpf(cliente.getCpf()) != null) {
            errors.add("cpf já está cadastrado.");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("parametros invalidos", errors);
        }

        clienteRepository.save(cliente);

        mensagemResponse.setStatus(201);
        mensagemResponse.setMessage("sucess");
        mensagemResponse.setDetails(detalhes);

        return new ResponseEntity<>(mensagemResponse, HttpStatus.CREATED);
    }


    @RequestMapping("update")
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MensagemResponse> updateCliente(@RequestBody Cliente cliente) {
        MensagemResponse mensagemResponse = new MensagemResponse();
        List<String> detalhes = new ArrayList<>();
        List<String> errors = new ArrayList<>();


       if (!clienteRepository.existsById(cliente.getId())){

           mensagemResponse.setStatus(400);

           mensagemResponse.setMessage("erro");
           if (cliente.getId()==0){
               detalhes.add("parementro id nao definido");
           }else {
               detalhes.add("id invalido");
           }

           mensagemResponse.setDetails(detalhes);

           return new ResponseEntity<>(mensagemResponse, HttpStatus.BAD_REQUEST);

       }


        clienteRepository.existsById(cliente.getId());


        if (cliente.getNome() == null) {
            errors.add("nome é obrigatório.");
        }
        if (cliente.getEmail() == null) {
            errors.add("email é obrigatório.");
        }
        if (cliente.getCpf() == null) {
            errors.add("cpf é obrigatório.");
        }
        if (cliente.getSenha() == null) {
            errors.add("senha é obrigatório.");
        }

        if (cliente.getStatusConta() == null){
            errors.add("statusConta é obrigatório.");
        }

        if (clienteRepository.existsByEmailAndIdNot(cliente.getEmail(), cliente.getId())) {
            errors.add("E-mail já associado a outro cliente.");
        }

        if (clienteRepository.existsByCpfAndIdNot(cliente.getCpf(), cliente.getId())) {
            errors.add("CPF já associado a outro cliente.");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("parametros invalidos", errors);
        }


        clienteRepository.save(cliente);

        mensagemResponse.setStatus(200);
        mensagemResponse.setMessage("sucess");
        mensagemResponse.setDetails(detalhes);

        return new ResponseEntity<>(mensagemResponse, HttpStatus.OK);
    }

    @DeleteMapping
    @RequestMapping("delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MensagemResponse> deleteCliente(@PathVariable String id) {
        MensagemResponse mensagemResponse = new MensagemResponse();
        List<String> detalhes = new ArrayList<>();
        long longId = 0;

        try {
            longId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            detalhes.add("id not INT");
        }
        if (!detalhes.isEmpty()) {
            throw new ValidationException("parametro invalido", detalhes);
        }


        if(!clienteRepository.existsById(longId)){
            mensagemResponse.setStatus(400);
            mensagemResponse.setMessage("erro");
            detalhes.add("Id não existe");
            mensagemResponse.setDetails(detalhes);
            return new ResponseEntity<>(mensagemResponse, HttpStatus.BAD_REQUEST);
        }

        clienteRepository.deleteById(longId);
        mensagemResponse.setStatus(200);
        mensagemResponse.setMessage("sucess");
        mensagemResponse.setDetails(detalhes);

        return new ResponseEntity<>(mensagemResponse, HttpStatus.BAD_REQUEST);
    }

    @GetMapping
    @RequestMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<Cliente> buscarCliente(@PathVariable String id){
        List<String> errors = new ArrayList<>();
        long longId = 0;

        try {
            longId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            errors.add("id not INT");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("parametro invalido", errors);
        }

        return clienteRepository.findById(longId);

    }


}
