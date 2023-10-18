package com.destiny.controller;

import com.destiny.model.Cliente;
import com.destiny.model.CustomUserDetails;
import com.destiny.model.Endereco;
import com.destiny.model.MensagemResponse;
import com.destiny.model.StatusConta;
import com.destiny.model.TipoConta;
import com.destiny.model.ValidationException;
import com.destiny.repository.ClienteRepository;
import com.destiny.repository.EnderecoRepository;
import com.destiny.repository.UsuarioRepository;

import lombok.var;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @GetMapping("/registra-me")
    public String telaRegistarCliente() {

        return "cliente/registroCliente";
    }

    @GetMapping("/clienteList")
    public List<Map<String, Object>> list() {
        return clienteRepository.findAllCustom();
    }

    @GetMapping("/listDetalhada")
    public List<Cliente> listAllDetalhes() {
        return clienteRepository.findAll();
    }

    @GetMapping("/alterar")
    public String telaAlterar() {

        return "cliente/AlterarCliente.html";
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MensagemResponse> insertCliente(@RequestBody Cliente cliente) {
        List<String> errors = new ArrayList<>();
        MensagemResponse mensagemResponse = new MensagemResponse();
        List<String> detalhes = new ArrayList<>();

        cliente.setStatusConta(StatusConta.ATIVA);
        cliente.setTipoConta(TipoConta.CLIENTE);

        List<Endereco> enderecos = cliente.getEnderecos();
        Boolean enderecoEntregaFaturamento;

        if (enderecos.size() == 1) {
            enderecoEntregaFaturamento = true;
        }
        cliente.setEnderecos(null);

        if (cliente.getNome() == null) {
            errors.add("nome é obrigatório.");
        }
        if (cliente.getEmail() == null) {
            errors.add("email é obrigatório.");
        }
        if (cliente.getCpf() == null || cliente.getCpf().isBlank()) {
            errors.add("cpf é obrigatório.");
        }
        if (cliente.getSenha() == null) {
            errors.add("senha é obrigatório.");
        }

        if (cliente.getDataNacimento() == null) {
            errors.add("Data de nacimento é obrigatório.");
        }

        if (enderecos.isEmpty()) {
            errors.add("O endereço é obrigatório.");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("parametros invalidos", errors);
        }

        if (clienteRepository.findByEmail(cliente.getEmail()) != null
                || usuarioRepository.findByEmail(cliente.getEmail()) != null) {
            errors.add("email já está cadastrado.");
        }

        if (clienteRepository.findByCpf(cliente.getCpf()) != null) {
            errors.add("cpf já está cadastrado.");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("parametros invalidos", errors);
        }

        clienteRepository.save(cliente);

        for (Endereco endereco : enderecos) {

            endereco.setCliente(cliente);

            if (enderecos.size() == 1) {
                Endereco endereco2 = new Endereco(endereco);
                endereco2.setTipo(Endereco.tipoEndereco.FATURAMENTO);
                endereco2.setPrincipal(false);
                enderecoRepository.save(endereco2);

                endereco.setTipo(Endereco.tipoEndereco.ENTREGA);
                endereco.setPrincipal(true);
                endereco.setCliente(cliente);
                enderecoRepository.save(endereco);
            }

            enderecoRepository.save(endereco);

        }

        mensagemResponse.setStatus(201);
        mensagemResponse.setMessage("sucess");
        mensagemResponse.setDetails(detalhes);

        return new ResponseEntity<>(mensagemResponse, HttpStatus.CREATED);
    }

    @PutMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MensagemResponse> updateCliente(@RequestBody Cliente cliente) {
        MensagemResponse mensagemResponse = new MensagemResponse();
        List<String> detalhes = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        if (!clienteRepository.existsById(cliente.getId())) {

            mensagemResponse.setStatus(400);

            mensagemResponse.setMessage("erro");
            if (cliente.getId() == 0) {
                detalhes.add("parementro id nao definido");
            } else {
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

        if (cliente.getStatusConta() == null) {
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

    @DeleteMapping("/delete/{id}")
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

        if (!clienteRepository.existsById(longId)) {
            mensagemResponse.setStatus(400);
            mensagemResponse.setMessage("erro");
            detalhes.add("Id não existe");
            mensagemResponse.setDetails(detalhes);
            return new ResponseEntity<>(mensagemResponse, HttpStatus.BAD_REQUEST);
        }

        clienteRepository.deleteById(longId);
        mensagemResponse.setStatus(200);
        mensagemResponse.setMessage("success");
        mensagemResponse.setDetails(detalhes);

        return new ResponseEntity<>(mensagemResponse, HttpStatus.OK);
    }

    @PostMapping("/add/endereco")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MensagemResponse> addECliente(@RequestBody Endereco endereco) {

        List<String> errors = new ArrayList<>();
        MensagemResponse mensagemResponse = new MensagemResponse();
        List<String> detalhes = new ArrayList<>();

        System.out.println(endereco);

        mensagemResponse.setStatus(200);
        mensagemResponse.setMessage("sucess");
        mensagemResponse.setDetails(detalhes);

        return new ResponseEntity<>(mensagemResponse, HttpStatus.OK);
    }

    @DeleteMapping("/delete/endereco/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MensagemResponse> adicinarEnderecoCliente(@PathVariable String id) {

        List<String> errors = new ArrayList<>();
        MensagemResponse mensagemResponse = new MensagemResponse();
        List<String> detalhes = new ArrayList<>();

        long longId = 0;

        System.out.println(longId);

        try {
            longId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            errors.add("id not INT");
        }

        enderecoRepository.updateStatusEndereco(StatusConta.INATIVA, longId);

        mensagemResponse.setStatus(200);
        mensagemResponse.setMessage("sucess");
        mensagemResponse.setDetails(detalhes);

        return new ResponseEntity<>(mensagemResponse, HttpStatus.OK);
    }

    @PutMapping("/padrao/endereco/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MensagemResponse> alterarEnderecoPadrao(@PathVariable String id) {
        List<String> errors = new ArrayList<>();
        MensagemResponse mensagemResponse = new MensagemResponse();
        List<String> detalhes = new ArrayList<>();

        try {

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth instanceof AnonymousAuthenticationToken) {
                mensagemResponse.setStatus(200);
                mensagemResponse.setMessage("erro");
                detalhes.add("usuario não esta autenticado");
                mensagemResponse.setDetails(detalhes);
                return new ResponseEntity<>(mensagemResponse, HttpStatus.ACCEPTED);
            }
            long longId = Long.parseLong(id);

            var userDetails = (CustomUserDetails) auth.getPrincipal();

            enderecoRepository.updateAllEnderecoPadrao(userDetails.getId());
            enderecoRepository.updateEnderecoPadrao(longId);

            mensagemResponse.setStatus(200);
            mensagemResponse.setMessage("success");
            mensagemResponse.setDetails(detalhes);

            return new ResponseEntity<>(mensagemResponse, HttpStatus.OK);
        } catch (NumberFormatException e) {
            errors.add("id not INT");
        } catch (Exception e) {

        }

        return new ResponseEntity<>(mensagemResponse, HttpStatus.OK);

    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<Cliente> buscarCliente(@PathVariable String id) {
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
