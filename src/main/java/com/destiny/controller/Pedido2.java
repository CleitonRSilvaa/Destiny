package com.destiny.controller;

import java.util.List;

import lombok.Data;

import javax.persistence.*;

import com.destiny.model.Pedido;

@Data
@Entity
public class Pedido2 {

  @Data
  @Entity
  public static class ItemPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // Adicione um identificador para a entidade ItemPedido
    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;
    private int quantidade;
    private String nome;
    private double avaliacao;
    private String descricao;
    private double preco;
    private int estoque;
    private String imagemPrincipal;

    // getters e setters

    @Override
    public String toString() {
      return "ItemPedido{" +
          "id=" + id +
          ", quantidade=" + quantidade +
          ", nome='" + nome + '\'' +
          ", avaliacao=" + avaliacao +
          ", descricao='" + descricao + '\'' +
          ", preco=" + preco +
          ", estoque=" + estoque +
          ", imagemPrincipal='" + imagemPrincipal + '\'' +
          '}';
    }
  }

  @Data
  @Entity
  public static class Endereco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // Adicione um identificador para a entidade Endereco
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;
    private String logradouro;
    private String bairro;
    private String cidade;
    private String uf;
    private String complemento;

    // getters e setters

    @Override
    public String toString() {
      return "Endereco{" +
          "id=" + id +
          ", logradouro='" + logradouro + '\'' +
          ", bairro='" + bairro + '\'' +
          ", cidade='" + cidade + '\'' +
          ", uf='" + uf + '\'' +
          ", complemento='" + complemento + '\'' +
          '}';
    }
  }

  @Data
  @Entity
  public static class CartaoDeCredito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // Adicione um identificador para a entidade CartaoDeCredito
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;
    private String numero;
    private String nome;
    private String validade;
    private String cvv;

    // getters e setters

    @Override
    public String toString() {
      return "CartaoDeCredito{" +
          "id=" + id +
          ", numero='" + numero + '\'' +
          ", nome='" + nome + '\'' +
          ", validade='" + validade + '\'' +
          ", cvv='" + cvv + '\'' +
          '}';
    }
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id; // Adicione um identificador para a entidade Pedido2

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "pedido_id") // Nome da coluna que faz referência ao Pedido2 na tabela ItemPedido
  private List<ItemPedido> carrinhoPedido;

  private String idDoCliente;
  private double valorTotal;
  private double valorFretePedido;
  private String formaPagamento;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "endereco_id") // Nome da coluna que faz referência ao Pedido2 na tabela Endereco
  private Endereco endereco;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "cartao_id") // Nome da coluna que faz referência ao Pedido2 na tabela CartaoDeCredito
  private CartaoDeCredito cartaoDeCredito;

  private int numeroParcelas;

  @Override
  public String toString() {
    return "Pedido2{" +
        "id=" + id +
        ", carrinhoPedido=" + carrinhoPedido +
        ", idDoCliente='" + idDoCliente + '\'' +
        ", valorTotal=" + valorTotal +
        ", valorFretePedido=" + valorFretePedido +
        ", formaPagamento='" + formaPagamento + '\'' +
        ", endereco=" + endereco +
        ", cartaoDeCredito=" + cartaoDeCredito +
        ", numeroParcelas=" + numeroParcelas +
        '}';
  }
}