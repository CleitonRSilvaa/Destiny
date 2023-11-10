package com.destiny.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class PedidoDetalhe {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "pedido_id", nullable = false)
  private Pedido pedido;

  @NotNull
  @Column(length = 200)
  private String nome;

  @NotNull
  private BigDecimal valor;

  @NotNull
  private int quantidade;

  private String img;

  private long produtoId;

  @Override
  public String toString() {
    return "PedidoDetalhe{" +
        "id=" + id +
        ", pedidoId=" + (pedido != null ? pedido.getId() : "null") +
        ", valor=" + valor +
        ", quantidade=" + quantidade +
        '}';
  }

}
