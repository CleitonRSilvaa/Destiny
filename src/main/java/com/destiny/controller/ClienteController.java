package com.destiny.controller;

import com.destiny.model.Cliente;
import com.destiny.model.CustomUserDetails;
import com.destiny.model.Endereco;
import com.destiny.model.MensagemResponse;
import com.destiny.model.Pedido;
import com.destiny.model.StatusConta;
import com.destiny.model.TipoConta;
import com.destiny.model.ValidationException;
import com.destiny.repository.ClienteRepository;
import com.destiny.repository.EnderecoRepository;
import com.destiny.repository.PedidoDetalheRepository;
import com.destiny.repository.PedidoRepository;
import com.destiny.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLDataException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PedidoDetalheRepository pedidoDetalheRepository;

    @GetMapping("/registra-me")
    public String telaRegistarCliente() {

        return "cliente/registroCliente";
    }

    @GetMapping("/alterar-senha")
    public String telaAlterarSenhaCliente(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            return "/login";
        }
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Optional optionalCliente = clienteRepository.findById(userDetails.getId());
        Cliente cliente = (Cliente) optionalCliente.get();
        cliente.setSenha("");
        cliente.setEnderecos(null);
        model.addAttribute("cliente", cliente);

        return "cliente/alterarSenhaCliente";
    }

    @GetMapping("/meus-pedidos")
    public String telaPedidos(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            return "/login";
        }
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Cliente cliente = clienteRepository.findById(userDetails.getId()).get();
        cliente.setSenha("");
        cliente.setEnderecos(null);

        cliente.getPedidos().sort((p1, p2) -> {
            if (p1.getId() > p2.getId()) {
                return -1;
            }
            if (p1.getId() < p2.getId()) {
                return 1;
            }
            return 0;
        });

        model.addAttribute("cliente", cliente);
        model.addAttribute("usuario", userDetails);

        return "cliente/meusPedidos";
    }

    @GetMapping("/detalhes-pedido/{id}")
    public String telaPedidoDetalhes(@PathVariable Long id, Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            return "/login";
        }
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Cliente cliente = clienteRepository.findById(userDetails.getId()).get();
        cliente.setSenha("");
        cliente.setEnderecos(null);
        cliente.setPedidos(null);
        Pedido pedido = pedidoRepository.findById(id).get();
        model.addAttribute("pedido", pedido);
        BigDecimal valorParcela = new BigDecimal(0);
        if (pedido.getMetodoPagamento().equals("CARTAO")) {
            BigDecimal parcelas = new BigDecimal(pedido.getParcelas());

            valorParcela = pedido.getValorTotal().divide(parcelas, 2, RoundingMode.HALF_UP);

        }

        Endereco endereco = enderecoRepository.findById(pedido.getEnderecoEntregaId()).get();

        model.addAttribute("cliente", cliente);
        model.addAttribute("endereco", endereco);
        model.addAttribute("usuario", userDetails);
        model.addAttribute("valorParcela", valorParcela.floatValue());
        return "cliente/detalhesPedido";
    }

    @PostMapping("/alterar-senha")
    public ResponseEntity<MensagemResponse> alterarSenha(@RequestBody Cliente.AlterarSenhaDTO clienteUpdate) {

        List<String> errors = new ArrayList<>();
        MensagemResponse mensagemResponse = new MensagemResponse();
        List<String> detalhes = new ArrayList<>();

        Cliente cliente = clienteRepository.findByEmail(clienteUpdate.getEmail());

        if (cliente == null) {
            mensagemResponse.setStatus(400);
            mensagemResponse.setMessage("error");
            mensagemResponse.setDetails(detalhes);
            return new ResponseEntity<>(mensagemResponse, HttpStatus.BAD_REQUEST);
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Verifica se a senha antiga informada é a mesma do banco
        if (!encoder.matches(clienteUpdate.getSenhaAntiga(), cliente.getSenha())) {
            detalhes.add("Senha atual incorreta.");
            mensagemResponse.setStatus(400);
            mensagemResponse.setMessage("paramentro-invalido");
            mensagemResponse.setDetails(detalhes);
            return new ResponseEntity<>(mensagemResponse, HttpStatus.BAD_REQUEST);
        }

        // Atualiza a senha e salva no banco
        cliente.setSenha(clienteUpdate.getSenhaNova());
        clienteRepository.save(cliente);

        detalhes.add("Senha alterada com sucesso!");
        mensagemResponse.setStatus(200);
        mensagemResponse.setMessage("success");
        mensagemResponse.setDetails(detalhes);
        return new ResponseEntity<>(mensagemResponse, HttpStatus.OK);
    }

    @GetMapping("/clienteList")
    public List<Map<String, Object>> list() {
        return clienteRepository.findAllCustom();
    }

    @GetMapping("/listDetalhada")
    public List<Cliente> listAllDetalhes() {
        return clienteRepository.findAll();
    }

    @GetMapping("/perfil")
    public String telaAlterar(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            return "/login";
        }
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Optional optionalCliente = clienteRepository.findById(userDetails.getId());
        Cliente cliente = (Cliente) optionalCliente.get();
        cliente.setSenha("");
        cliente.setEnderecos(null);
        List<Endereco> enderecos = enderecoRepository.findByClienteIdAndStatusAndTipo(cliente.getId(),
                StatusConta.ATIVA,
                Endereco.tipoEndereco.ENTREGA);
        enderecos.sort((e1, e2) -> {
            if (e1.getPrincipal() && !e2.getPrincipal()) {
                return -1;
            } else if (!e1.getPrincipal() && e2.getPrincipal()) {
                return 1;
            }
            return 0;
        });

        cliente.setEnderecos(enderecos);
        model.addAttribute("cliente", cliente);
        model.addAttribute("usuario", userDetails);
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
                endereco.setStatus(StatusConta.ATIVA);
                Endereco endereco2 = new Endereco(endereco);
                endereco2.setTipo(Endereco.tipoEndereco.FATURAMENTO);
                endereco2.setPrincipal(false);
                enderecoRepository.save(endereco2);

                endereco.setTipo(Endereco.tipoEndereco.ENTREGA);
                endereco.setPrincipal(true);
                endereco.setCliente(cliente);
                enderecoRepository.save(endereco);
            } else {
                endereco.setStatus(StatusConta.ATIVA);
                enderecoRepository.save(endereco);
            }

        }

        mensagemResponse.setStatus(201);
        mensagemResponse.setMessage("success");
        mensagemResponse.setDetails(detalhes);

        return new ResponseEntity<>(mensagemResponse, HttpStatus.CREATED);
    }

    @PutMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MensagemResponse> updateCliente(@RequestBody Cliente cliente) {
        MensagemResponse mensagemResponse = new MensagemResponse();
        List<String> detalhes = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof AnonymousAuthenticationToken) {
            mensagemResponse.setStatus(200);
            mensagemResponse.setMessage("erro");
            detalhes.add("usuario não esta autenticado");
            mensagemResponse.setDetails(detalhes);
            return new ResponseEntity<>(mensagemResponse, HttpStatus.ACCEPTED);
        }

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

        if (!clienteRepository.existsById(userDetails.getId())) {
            mensagemResponse.setStatus(400);
            mensagemResponse.setMessage("erro");
            if (userDetails.getId() == 0) {
                detalhes.add("parementro id nao definido");
            } else {
                detalhes.add("id invalido");
            }
            mensagemResponse.setDetails(detalhes);
            return new ResponseEntity<>(mensagemResponse, HttpStatus.BAD_REQUEST);

        }

        System.out.println("Clienteeeeeeee " + cliente);

        Optional optionalCliente = clienteRepository.findById(userDetails.getId());

        Cliente cliente2 = (Cliente) optionalCliente.get();

        if (cliente.getNome().isBlank()) {
            errors.add("nome é obrigatório.");
        }
        if (cliente.getDataNacimento() == null) {
            errors.add("data de nascimento é obrigatório.");
        }
        if (cliente.getGenero().isBlank()) {
            errors.add("cpf é obrigatório.");
        }

        if (clienteRepository.existsByEmailAndIdNot(cliente2.getEmail(),
                cliente2.getId())) {
            errors.add("E-mail já associado a outro cliente.");
        }

        if (clienteRepository.existsByCpfAndIdNot(cliente2.getCpf(),
                cliente2.getId())) {
            errors.add("CPF já associado a outro cliente.");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("parametros invalidos", errors);
        }

        cliente2.setNome(cliente.getNome());
        cliente2.setDataNacimento(cliente.getDataNacimento());
        cliente2.setGenero(cliente.getGenero());
        clienteRepository.save(cliente2);

        mensagemResponse.setStatus(200);
        mensagemResponse.setMessage("success");
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

        try {

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth instanceof AnonymousAuthenticationToken) {
                mensagemResponse.setStatus(200);
                mensagemResponse.setMessage("erro");
                detalhes.add("usuario não esta autenticado");
                mensagemResponse.setDetails(detalhes);
                return new ResponseEntity<>(mensagemResponse, HttpStatus.ACCEPTED);
            }

            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

            Optional<Cliente> cliente = clienteRepository.findById(userDetails.getId());
            endereco.setCliente(cliente.get());
            endereco.setStatus(StatusConta.ATIVA);

            enderecoRepository.save(endereco);
            mensagemResponse.setStatus(200);
            mensagemResponse.setMessage("success");
            mensagemResponse.setDetails(detalhes);

            return new ResponseEntity<>(mensagemResponse, HttpStatus.OK);
        } catch (NumberFormatException e) {
            detalhes.clear();
            detalhes.add("id not INT");
            mensagemResponse.setStatus(400);
            mensagemResponse.setMessage("erro");
            mensagemResponse.setDetails(detalhes);
            return new ResponseEntity<>(mensagemResponse, HttpStatus.OK);
        } catch (Exception e) {
            detalhes.clear();
            detalhes.add(e.getLocalizedMessage());
            mensagemResponse.setStatus(400);
            mensagemResponse.setMessage("erro");
            mensagemResponse.setDetails(detalhes);
            return new ResponseEntity<>(mensagemResponse, HttpStatus.OK);
        }

    }

    @DeleteMapping("/delete/endereco/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MensagemResponse> adicinarEnderecoCliente(@PathVariable String id) {

        List<String> errors = new ArrayList<>();
        MensagemResponse mensagemResponse = new MensagemResponse();
        List<String> detalhes = new ArrayList<>();

        long longId = 0;

        try {
            longId = Long.parseLong(id);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth instanceof AnonymousAuthenticationToken) {
                mensagemResponse.setStatus(200);
                mensagemResponse.setMessage("erro");
                detalhes.add("usuario não esta autenticado");
                mensagemResponse.setDetails(detalhes);
                return new ResponseEntity<>(mensagemResponse, HttpStatus.ACCEPTED);
            }

            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

            Cliente cliente = clienteRepository.findById(userDetails.getId()).get();

            List enderecos = enderecoRepository.findByClienteIdAndStatus(cliente.getId(), StatusConta.ATIVA);

            if (enderecos.size() == 1) {

                mensagemResponse.setStatus(400);
                mensagemResponse.setMessage("Único endereço cadastrado não pode ser deleteado");

                mensagemResponse.setDetails(detalhes);
                return new ResponseEntity<>(mensagemResponse, HttpStatus.OK);

            }
            enderecoRepository.updateStatusEndereco(StatusConta.INATIVA, longId);

            List<Endereco> enderecos2 = enderecoRepository.findByClienteIdAndStatus(userDetails.getId(),
                    StatusConta.ATIVA);

            if (enderecos2.size() == 1) {
                for (Endereco e : enderecos2) {
                    enderecoRepository.updateAllEnderecoPadrao(userDetails.getId());
                    enderecoRepository.updateEnderecoPadrao(e.getId());
                }

            }

            mensagemResponse.setStatus(200);
            mensagemResponse.setMessage("success");
            mensagemResponse.setDetails(detalhes);

            return new ResponseEntity<>(mensagemResponse, HttpStatus.OK);
        } catch (NumberFormatException e) {
            detalhes.clear();
            detalhes.add("id not INT");
            mensagemResponse.setStatus(400);
            mensagemResponse.setMessage("erro");
            mensagemResponse.setDetails(detalhes);
            return new ResponseEntity<>(mensagemResponse, HttpStatus.OK);
        } catch (Exception e) {
            detalhes.clear();
            detalhes.add(e.getLocalizedMessage());
            mensagemResponse.setStatus(400);
            mensagemResponse.setMessage("erro");
            mensagemResponse.setDetails(detalhes);
            return new ResponseEntity<>(mensagemResponse, HttpStatus.OK);
        }

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

            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

            enderecoRepository.updateAllEnderecoPadrao(userDetails.getId());
            enderecoRepository.updateEnderecoPadrao(longId);

            mensagemResponse.setMessage("success");
            mensagemResponse.setDetails(detalhes);

            return new ResponseEntity<>(mensagemResponse, HttpStatus.OK);
        } catch (NumberFormatException e) {
            errors.add("id not INT");
        } catch (Exception e) {

        }

        return new ResponseEntity<>(mensagemResponse, HttpStatus.OK);

    }

}
