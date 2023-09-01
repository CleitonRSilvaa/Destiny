package com.destiny.component;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private RequestCache requestCache = new HttpSessionRequestCache();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        SavedRequest savedRequest = requestCache.getRequest(request, response);

        if (savedRequest != null) {
            // Redireciona para a última página solicitada antes do login
            String redirectURL = determineTargetUrl(authentication);
            response.sendRedirect(redirectURL);
        } else {
            // Se não houver uma página salva, use a lógica padrão
            String redirectURL = determineTargetUrl(authentication);
            response.sendRedirect(redirectURL);
        }
    }

    protected String determineTargetUrl(Authentication authentication) {
        Set<String> roles = authentication.getAuthorities().stream()
                .map(r -> r.getAuthority())
                .collect(Collectors.toSet());

        if (roles.contains("ROLE_ADMIN")) {
            return "/admin/dashboard";
        } else if (roles.contains("ROLE_ESTOQUISTA")) {
            return "/estoque/dashboard";
        } else {
            return "/home"; // para outros usuários ou default
        }
    }
}
