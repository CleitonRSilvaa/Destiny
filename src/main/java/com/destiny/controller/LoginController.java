package com.destiny.controller;

import com.destiny.model.CustomUserDetails;
import com.destiny.model.TipoConta;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    String login() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!(auth instanceof AnonymousAuthenticationToken)) {
            if (auth.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
                userDetails.getNome();

                if (userDetails.getTipoConta().equals(TipoConta.ADMIN)) {
                    return "redirect:/admin/dashboard";
                }
                if (userDetails.getTipoConta().equals(TipoConta.ESTOQUISTA)) {
                    return "redirect:/estoque/dashboard";
                }
                if (userDetails.getTipoConta().equals(TipoConta.CLIENTE)) {
                    return "redirect:/";
                }
            }

        }

        return "login";
    }

}
