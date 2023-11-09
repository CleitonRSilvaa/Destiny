package com.destiny.controller;

import com.destiny.model.MensagemResponse;
import com.destiny.model.StatusConta;
import com.destiny.model.Usuario;
import com.destiny.model.ValidationException;
import com.destiny.repository.UsuarioRepository;
import com.destiny.utils.CpfValidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("usuario")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/lista")
    public String listarSenhas(@RequestParam(required = false) String nomeBusca, Model model) {
        List<UsuarioRepository.UsuarioResumo> listaDeUsuarios;

        if (nomeBusca != null && !nomeBusca.trim().isEmpty()) {
            listaDeUsuarios = usuarioRepository.findByNomeContainingIgnoreCase(nomeBusca);
        } else {
            listaDeUsuarios = usuarioRepository.findAllCustom();
        }

        model.addAttribute("listaDeUsuarios", listaDeUsuarios);

        return "admin/admin-menager_usuarios";
    }

    @GetMapping("usuarioList")
    public List<Usuario> list() {
        return usuarioRepository.findAll();
    }

    @ResponseBody // Assegura que a resposta será o corpo da mensagem
    @GetMapping("/listDetalhada")
    public List<Usuario> listAllDetalhes() {
        return usuarioRepository.findAll();
    }

    @ResponseBody
    @PostMapping("add")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MensagemResponse> insertCliente(@RequestBody Usuario usuario) {
        List<String> errors = new ArrayList<>();
        MensagemResponse mensagemResponse = new MensagemResponse();
        List<String> detalhes = new ArrayList<>();

        usuario.setStatusConta(StatusConta.ATIVA);

        usuario.setCpf(usuario.getCpf().replaceAll("[^0-9]", ""));

        if (usuario.getNome() == null) {
            errors.add("nome é obrigatório.");
        }
        if (usuario.getEmail() == null) {
            errors.add("email é obrigatório.");
        }
        if (usuario.getCpf() == null || usuario.getCpf().trim().isEmpty()) {
            errors.add("cpf é obrigatório.");
        }

        if (usuario.getSenha() == null) {
            errors.add("senha é obrigatório.");
        }
        if (usuario.getTipoConta() == null) {
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
        mensagemResponse.setMessage("success");
        mensagemResponse.setDetails(detalhes);

        return new ResponseEntity<>(mensagemResponse, HttpStatus.CREATED);
    }

    @ResponseBody
    @PutMapping("update")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MensagemResponse> updateCliente(@RequestBody Usuario usuario) {
        MensagemResponse mensagemResponse = new MensagemResponse();
        List<String> detalhes = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        if (!usuarioRepository.existsById(usuario.getId())) {

            mensagemResponse.setStatus(400);

            mensagemResponse.setMessage("erro");
            if (usuario.getId() == 0) {
                detalhes.add("parementro id nao definido");
            } else {
                detalhes.add("id invalido");
            }

            mensagemResponse.setDetails(detalhes);

            return new ResponseEntity<>(mensagemResponse, HttpStatus.BAD_REQUEST);

        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Usuario> userInDb = usuarioRepository.findById(usuario.getId());
        Usuario existingUser = userInDb.get();

        usuario.setCpf(usuario.getCpf().replaceAll("[^0-9]", ""));

        if (usuario.getNome() == null) {
            errors.add("nome é obrigatório.");
        }
        if (usuario.getEmail() == null) {
            errors.add("email é obrigatório.");
        }

        if (usuario.getCpf() == null || usuario.getCpf().trim().isEmpty()) {
            errors.add("cpf é obrigatório.");
        }

        if (usuario.getSenha() != null && !usuario.getSenha().trim().isEmpty()) {
            existingUser.setSenha(usuario.getSenha());
        }

        if (usuario.getTipoConta() == null) {
            errors.add("tipoConta é obrigatório.");
        }

        if (usuario.getStatusConta() == null) {
            errors.add("statusConta é obrigatório.");
        }

        if (usuarioRepository.existsByEmailAndIdNot(usuario.getEmail(), usuario.getId())) {
            errors.add("E-mail já associado a outro usuario.");
        }

        if (usuarioRepository.existsByCpfAndIdNot(usuario.getCpf(), usuario.getId())) {
            errors.add("CPF já associado a outro usuario.");
        }

        if (CpfValidator.isValid(usuario.getCpf())) {
            errors.add("CPF inválido.");
        }

        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();

        GrantedAuthority firstAuthority = authorities.iterator().next();
        String authorityValue = firstAuthority.getAuthority();

        boolean hasSameRole = ("ROLE_" + usuario.getTipoConta()).equals(authorityValue);

        boolean isSameUser = auth.getName().equals(usuario.getEmail());

        if (!hasSameRole && isSameUser) {
            errors.add("Você não tem permissão para editar seu próprio grupo.");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("parametros invalidos", errors);
        }

        usuarioRepository.save(usuario);

        mensagemResponse.setStatus(200);
        mensagemResponse.setMessage("success");
        mensagemResponse.setDetails(detalhes);

        return new ResponseEntity<>(mensagemResponse, HttpStatus.OK);
    }

    @ResponseBody
    @DeleteMapping("delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MensagemResponse> deleteCliente(@PathVariable String id) {
        MensagemResponse mensagemResponse = new MensagemResponse();
        List<String> detalhes = new ArrayList<>();
        long longId = 0;

        try {
            longId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            detalhes.add("id not int");
        }
        if (!detalhes.isEmpty()) {
            throw new ValidationException("parametro invalido", detalhes);
        }

        if (!usuarioRepository.existsById(longId)) {
            mensagemResponse.setStatus(400);
            mensagemResponse.setMessage("erro");
            detalhes.add("Id não existe");
            mensagemResponse.setDetails(detalhes);
            return new ResponseEntity<>(mensagemResponse, HttpStatus.BAD_REQUEST);
        }

        usuarioRepository.deleteById(longId);
        mensagemResponse.setStatus(200);
        mensagemResponse.setMessage("success");
        mensagemResponse.setDetails(detalhes);

        return new ResponseEntity<>(mensagemResponse, HttpStatus.OK);
    }

    @ResponseBody
    @PostMapping("/updateStatus")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MensagemResponse> updateStatus(@RequestBody StatusUpdateRequest statusConta) {
        MensagemResponse mensagemResponse = new MensagemResponse();
        List<String> detalhes = new ArrayList<>();
        long longId = 0;

        try {
            longId = Long.valueOf(statusConta.getId());
        } catch (NumberFormatException e) {
            detalhes.add("id not INT");
        }
        if (!detalhes.isEmpty()) {
            throw new ValidationException("parametro invalido", detalhes);
        }

        if (!usuarioRepository.existsById(longId)) {
            mensagemResponse.setStatus(400);
            mensagemResponse.setMessage("erro");
            detalhes.add("Id não existe");
            mensagemResponse.setDetails(detalhes);
            return new ResponseEntity<>(mensagemResponse, HttpStatus.BAD_REQUEST);
        }

        usuarioRepository.updateStatusConta(statusConta.getStatus(), longId);
        mensagemResponse.setStatus(200);
        mensagemResponse.setMessage("success");
        mensagemResponse.setDetails(detalhes);

        return new ResponseEntity<>(mensagemResponse, HttpStatus.OK);
    }

    // @ResponseBody
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<Usuario> buscarCliente(@PathVariable String id) {
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

    public static class StatusUpdateRequest {

        private StatusConta status;
        private Long id;

        public StatusConta getStatus() {
            return status;
        }

        public void setStatus(StatusConta status) {
            this.status = status;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }
}
