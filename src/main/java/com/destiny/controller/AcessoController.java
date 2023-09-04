package com.destiny.controller;

import com.destiny.model.MensagemResponse;
import com.destiny.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("")
public class AcessoController {

    @GetMapping("/admin/dashboard")
    @ResponseStatus(HttpStatus.OK)
    public String admin(@RequestParam(required = false) String nomeBusca, Model model) {
        return "admin/index";
    }

    @GetMapping("/estoque/dashboard")
    @ResponseStatus(HttpStatus.OK)
    public String estoque(@RequestParam(required = false) String nomeBusca, Model model) {
        return "estoque/index";
    }

    @GetMapping("/home")
    @ResponseStatus(HttpStatus.OK)
    public String home(@RequestParam(required = false) String nomeBusca, Model model, HttpSession session) {
        if (session.getAttribute("message") != null) {
            model.addAttribute("errorMessage", session.getAttribute("message"));
            session.removeAttribute("message"); // Limpa a mensagem da sessão após lê-la
        }
        return "index";
    }

}
