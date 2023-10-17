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

  // Construtor para Cliente
  public CustomUserDetails(Cliente cliente) {
    super(cliente.getEmail(), cliente.getSenha(), getAuthority(cliente));
    this.nome = cliente.getNome();
    this.tipoConta = cliente.getTipoConta();
  }

  public String getNome() {
    return nome;
  }

  public TipoConta getTipoConta() {
    return tipoConta;
  }

  private static Collection<? extends GrantedAuthority> getAuthority(Object user) {
    if (user instanceof Usuario) {
      return Collections
          .singletonList(new SimpleGrantedAuthority("ROLE_" + ((Usuario) user).getTipoConta().toString()));
    } else if (user instanceof Cliente) {
      // Aqui, assumi que Cliente também tem um campo tipoConta. Ajuste conforme
      // necessário.
      return Collections
          .singletonList(new SimpleGrantedAuthority("ROLE_" + ((Cliente) user).getTipoConta().toString()));
    }
    return Collections.emptyList();
  }

}
