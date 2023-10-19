package com.destiny.model;

import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
public class Endereco {

  public static enum tipoEndereco {
    ENTREGA,
    FATURAMENTO;
  }

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

  @Column(nullable = false)
  private tipoEndereco tipo;

  private Boolean principal;

  private StatusConta status;

  @ManyToOne
  @JoinColumn(name = "cliente_id", nullable = false)
  private Cliente cliente;

  public Endereco() {

  }

  public Endereco(Endereco outroEndereco) {
    this.id = outroEndereco.id; // Você pode ou não querer copiar o ID, dependendo do caso de uso
    this.cep = outroEndereco.cep;
    this.logradouro = outroEndereco.logradouro;
    this.complemento = outroEndereco.complemento;
    this.bairro = outroEndereco.bairro;
    this.localidade = outroEndereco.localidade;
    this.uf = outroEndereco.uf;
    this.numero = outroEndereco.numero;
    this.tipo = outroEndereco.tipo;
    this.principal = outroEndereco.principal;
    this.cliente = outroEndereco.cliente;
    this.status = outroEndereco.status;
  }

  @Override
  public String toString() {
    return "Endereco{" +
        "id=" + id +
        ", cep='" + cep + '\'' +
        ", logradouro='" + logradouro + '\'' +
        ", complemento='" + complemento + '\'' +
        ", bairro='" + bairro + '\'' +
        ", localidade='" + localidade + '\'' +
        ", uf='" + uf + '\'' +
        ", numero='" + numero + '\'' +
        ", cliente=" + (cliente != null ? cliente.getId() : "null") +
        '}';
  }

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
