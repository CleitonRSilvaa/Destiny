package com.destiny.controller;

import com.destiny.model.Cliente;
import com.destiny.model.CustomUserDetails;
import com.destiny.model.Endereco;
import com.destiny.model.MensagemResponse;
import com.destiny.model.Produto;
import com.destiny.model.StatusConta;
import com.destiny.model.StatusProduto;
import com.destiny.model.TipoConta;
import com.destiny.repository.ProdutoRepository;
import com.destiny.repository.UsuarioRepository;
import com.destiny.service.ClienteService;
import com.destiny.service.ProdutoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("")
public class AcessoController {

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private ClienteService clienteService;

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public String landingPage(Model model) {
        var listaProdutos = produtoService.buscarProdutosPorStatus(StatusProduto.ATIVO);
        model.addAttribute("produtoPage", listaProdutos);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails userDetails = null;
        if (auth.getPrincipal() instanceof CustomUserDetails) {
            userDetails = (CustomUserDetails) auth.getPrincipal();
        }

        model.addAttribute("usuario", userDetails);

        return "landingPage";
    }

    @GetMapping("/carrinho")
    @ResponseStatus(HttpStatus.OK)
    public String carrinhoCompras(Model model) {
        var listaProdutos = produtoService.buscarProdutosPorStatus(StatusProduto.ATIVO);
        model.addAttribute("produtoPage", listaProdutos);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails userDetails = null;
        if (auth.getPrincipal() instanceof CustomUserDetails) {
            userDetails = (CustomUserDetails) auth.getPrincipal();
            if (userDetails.getTipoConta().equals(TipoConta.CLIENTE)) {
                Cliente cliente = clienteService.getClienteBySection(auth);

                cliente.setEnderecos(clienteService.findByClienteIdAndStatusAndTipoOrderByPrincipalDesc(cliente));
                model.addAttribute("cliente", cliente);
            }
        }

        model.addAttribute("usuario", userDetails);

        return "carrinhoCompras";
    }

    @GetMapping("/checkout")
    @ResponseStatus(HttpStatus.OK)
    public String checkout(Model model) {
        var listaProdutos = produtoService.buscarProdutosPorStatus(StatusProduto.ATIVO);
        model.addAttribute("produtoPage", listaProdutos);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails userDetails = null;
        if (auth.getPrincipal() instanceof CustomUserDetails) {
            userDetails = (CustomUserDetails) auth.getPrincipal();
        }
        Cliente cliente = clienteService.getClienteBySection(auth);
        model.addAttribute("usuario", userDetails);

        cliente.setEnderecos(clienteService.findByClienteIdAndStatusAndTipoOrderByPrincipalDesc(cliente));
        model.addAttribute("cliente", cliente);

        return "pagCheckout";
    }

    @GetMapping("/admin/dashboard")
    @ResponseStatus(HttpStatus.OK)
    public String admin(@RequestParam(required = false) String nomeBusca, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails userDetails = null;
        if (auth.getPrincipal() instanceof CustomUserDetails) {
            userDetails = (CustomUserDetails) auth.getPrincipal();
        }

        model.addAttribute("usuario", userDetails);
        return "admin/index";
    }

    @GetMapping("/estoque/dashboard")
    @ResponseStatus(HttpStatus.OK)
    public String estoque(@RequestParam(required = false) String nomeBusca, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails userDetails = null;
        if (auth.getPrincipal() instanceof CustomUserDetails) {
            userDetails = (CustomUserDetails) auth.getPrincipal();
        }

        model.addAttribute("usuario", userDetails);
        return "estoque/index";
    }

    @GetMapping("/home")
    @ResponseStatus(HttpStatus.OK)
    public String home(@RequestParam(required = false) String nomeBusca, Model model, HttpSession session) {
        if (session.getAttribute("message") != null) {
            model.addAttribute("errorMessage", session.getAttribute("message"));
            session.removeAttribute("message"); // Limpa a mensagem da sessão após lê-la
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails userDetails = null;
        if (auth.getPrincipal() instanceof CustomUserDetails) {
            userDetails = (CustomUserDetails) auth.getPrincipal();
        }

        model.addAttribute("usuario", userDetails);
        return "index";
    }

}
