package com.destiny.service;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.destiny.model.Cliente;
import com.destiny.model.CustomUserDetails;
import com.destiny.model.StatusConta;
import com.destiny.model.Usuario;
import com.destiny.repository.ClienteRepository;
import com.destiny.repository.UsuarioRepository;

@Service

public class UserDetailsServiceImpl implements UserDetailsService {

  @Autowired
  private UsuarioRepository usuarioRepository;

  @Autowired
  private ClienteRepository clienteRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    UserDetails userDetails = null;

    Usuario usuario = usuarioRepository.findByEmail(email);
    if (usuario != null) {
      checkStatus(usuario);
      userDetails = new CustomUserDetails(usuario);
    } else {
      Cliente cliente = clienteRepository.findByEmail(email);
      if (cliente != null) {
        checkStatus(cliente);
        userDetails = new CustomUserDetails(cliente);
      }
    }

    if (userDetails == null) {
      throw new UsernameNotFoundException("Email ou senha incorretos");
    }

    return userDetails;
  }

  private void checkStatus(Object user) {
    if (user instanceof Usuario) {
      if (((Usuario) user).getStatusConta().equals(StatusConta.INATIVA)) {
        throw new DisabledException("Conta de usuário inativa");
      }
    }
    // Implemente uma verificação similar se Cliente também tiver um status.
  }

  private Collection<? extends GrantedAuthority> getAuthority(Object user) {
    if (user instanceof Usuario) {
      return Collections
          .singletonList(new SimpleGrantedAuthority("ROLE_" + ((Usuario) user).getTipoConta().toString()));
    } else if (user instanceof Cliente) {
      // Ajuste conforme a lógica para Cliente.
      return Collections.singletonList(new SimpleGrantedAuthority("ROLE_CLIENTE"));
    }
    return Collections.emptyList();
  }
}