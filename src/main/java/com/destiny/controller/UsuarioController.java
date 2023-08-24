package com.destiny.controller;

import com.destiny.model.MensagemResponse;
import com.destiny.model.Usuario;
import com.destiny.model.ValidationException;
import com.destiny.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("usuario")
public class UsuarioController {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @RequestMapping("usuarioList")
    @GetMapping
    public List<Map<String, Object>> list(){
        return usuarioRepository.findAllCustom();
    }


    @RequestMapping("/listDetalhada")
    @GetMapping
    public List<Usuario> listAllDetalhes(){
        return usuarioRepository.findAll();
    }

    @RequestMapping("add")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MensagemResponse> insertCliente(@RequestBody Usuario usuario) {
        List<String> errors = new ArrayList<>();
        MensagemResponse mensagemResponse = new MensagemResponse();
        List<String> detalhes = new ArrayList<>();

        usuario.setStatusConta((byte) 1);

        if (usuario.getNome() == null) {
            errors.add("nome é obrigatório.");
        }
        if (usuario.getEmail() == null) {
            errors.add("email é obrigatório.");
        }
        if (usuario.getCpf() == null) {
            errors.add("cpf é obrigatório.");
        }
        if (usuario.getSenha() == null) {
            errors.add("senha é obrigatório.");
        }
        if (usuario.getTipoConta() == -1){
            errors.add("tipoConta é obrigatório.");
        }

        if (usuarioRepository.findByEmail(usuario.getEmail()) != null) {
            errors.add("email já está cadastrado.");
        }

        if (usuarioRepository.findByCpf(usuario.getCpf()) != null) {
            errors.add("cpf já está cadastrado.");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("parametros invalidos", errors);
        }

        usuarioRepository.save(usuario);

        mensagemResponse.setStatus(201);
        mensagemResponse.setMessage("sucess");
        mensagemResponse.setDetails(detalhes);

        return new ResponseEntity<>(mensagemResponse, HttpStatus.CREATED);
    }


    @RequestMapping("update")
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MensagemResponse> updateCliente(@RequestBody Usuario usuario) {
        MensagemResponse mensagemResponse = new MensagemResponse();
        List<String> detalhes = new ArrayList<>();
        List<String> errors = new ArrayList<>();


        if (!usuarioRepository.existsById(usuario.getId())){

            mensagemResponse.setStatus(400);

            mensagemResponse.setMessage("erro");
            if (usuario.getId()==0){
                detalhes.add("parementro id nao definido");
            }else {
                detalhes.add("id invalido");
            }

            mensagemResponse.setDetails(detalhes);

            return new ResponseEntity<>(mensagemResponse, HttpStatus.BAD_REQUEST);

        }


        usuarioRepository.existsById(usuario.getId());


        if (usuario.getNome() == null) {
            errors.add("nome é obrigatório.");
        }
        if (usuario.getEmail() == null) {
            errors.add("email é obrigatório.");
        }
        if (usuario.getCpf() == null) {
            errors.add("cpf é obrigatório.");
        }
        if (usuario.getSenha() == null) {
            errors.add("senha é obrigatório.");
        }

        if (usuario.getTipoConta() == -1){
            errors.add("tipoConta é obrigatório.");
        }

        if (usuario.getStatusConta() == null){
            errors.add("statusConta é obrigatório.");
        }

        if (usuarioRepository.existsByEmailAndIdNot(usuario.getEmail(), usuario.getId())) {
            errors.add("E-mail já associado a outro usuario.");
        }

        if (usuarioRepository.existsByCpfAndIdNot(usuario.getCpf(), usuario.getId())) {
            errors.add("CPF já associado a outro usuario.");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("parametros invalidos", errors);
        }


        usuarioRepository.save(usuario);

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


        if(!usuarioRepository.existsById(longId)){
            mensagemResponse.setStatus(400);
            mensagemResponse.setMessage("erro");
            detalhes.add("Id não existe");
            mensagemResponse.setDetails(detalhes);
            return new ResponseEntity<>(mensagemResponse, HttpStatus.BAD_REQUEST);
        }

        usuarioRepository.deleteById(longId);
        mensagemResponse.setStatus(200);
        mensagemResponse.setMessage("sucess");
        mensagemResponse.setDetails(detalhes);

        return new ResponseEntity<>(mensagemResponse, HttpStatus.BAD_REQUEST);
    }

    @GetMapping
    @RequestMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<Usuario> buscarCliente(@PathVariable String id){
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

        return usuarioRepository.findById(longId);

    }
}
