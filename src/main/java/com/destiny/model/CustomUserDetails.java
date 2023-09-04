package com.destiny.model;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class CustomUserDetails extends User {
  private final String nome;
  private final TipoConta tipoConta;

  public CustomUserDetails(Usuario usuario) {
    super(usuario.getEmail(), usuario.getSenha(), getAuthority(usuario));
    this.nome = usuario.getNome();
    this.tipoConta = usuario.getTipoConta();
  }

  public String getNome() {
    return nome;
  }

  public TipoConta getTipoConta() {
    return tipoConta;
  }

  private static Collection<? extends GrantedAuthority> getAuthority(Usuario usuario) {
    return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getTipoConta().toString()));
  }

}
