package com.destiny.model;

import javax.persistence.*;
import lombok.Data;

@Data
@Entity
public class Endereco {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String cep;
  @Column(nullable = false)
  private String logradouro;

  private String complemento;

  @Column(nullable = false)
  private String bairro;

  @Column(nullable = false)
  private String localidade;

  @Column(nullable = false)
  private String uf;

  @Column(nullable = false)
  private String numero;

  @ManyToOne
  @JoinColumn(name = "cliente_id", nullable = false)
  private Cliente cliente;

  // // Construtores
  // public Endereco() {
  // }

  // public Endereco(String cep, String logradouro, String complemento, String
  // bairro,
  // String localidade, String uf) {
  // this.cep = cep;
  // this.logradouro = logradouro;
  // this.complemento = complemento;
  // this.bairro = bairro;
  // this.localidade = localidade;
  // this.uf = uf;
  // }

}
