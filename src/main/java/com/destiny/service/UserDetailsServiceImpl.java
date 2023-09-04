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

import com.destiny.model.CustomUserDetails;
import com.destiny.model.StatusConta;
import com.destiny.model.Usuario;
import com.destiny.repository.UsuarioRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  @Autowired
  private UsuarioRepository usuarioRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Usuario usuario = usuarioRepository.findByEmail(email);

    if (usuario == null) {
      throw new UsernameNotFoundException("Email ou senha incorretos");
    }

    if (usuario.getStatusConta().equals(StatusConta.INATIVA)) {
      throw new DisabledException("Conta de usuário inativa");
    }

    return new CustomUserDetails(usuario);
  }

  private Collection<? extends GrantedAuthority> getAuthority(Usuario usuario) {
    return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getTipoConta().toString()));
  }
}
