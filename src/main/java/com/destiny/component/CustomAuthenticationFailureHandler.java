package com.destiny.component;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException exception) throws IOException, ServletException {

    String redirectUrl = "/login?error";

    if (exception instanceof BadCredentialsException) {
      request.getSession().setAttribute("error", "Email ou senha incorretos");
    } else if (exception instanceof InternalAuthenticationServiceException || exception instanceof DisabledException) {
      request.getSession().setAttribute("error", exception.getMessage());
    }
    response.sendRedirect(redirectUrl);
  }
}
