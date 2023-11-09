package com.destiny.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;
import lombok.NonNull;

public class PedidoDTO {

  public static enum StatusPedido {
    PENDENTE,
    APROVADO,
    CANCELADO
  }

  @NotNull
  private long enderecoEntregaId;

  @ManyToOne
  @JoinColumn(name = "cliente_id", nullable = false)
  private long cli;

  @Column(nullable = false)
  private StatusPedido statusPedido;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private LocalDate dataPedido;

  @Column(nullable = false)
  private String metodoPagamento;

  @Column(nullable = false)
  private BigDecimal valorTotal;

  @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
  private List<PedidoDetalhe> pedidoDetalhes;

  public void setNumeroPedido() {
    String lUUID = String.format("%040d", new BigInteger(UUID.randomUUID().toString().replace("-", ""), 10));
    this.numeroPedido = lUUID;
  }

  @Override
  public String toString() {
    return "Pedido{" +
        "id=" + id +
        ", numeroPedido='" + numeroPedido + '\'' +
        ", enderecoEntregaId=" + enderecoEntregaId +
        ", clienteId=" + (cliente != null ? cliente.getId() : "null") +
        ", statusPedido=" + statusPedido +
        ", dataPedido=" + dataPedido +
        ", metodoPagamento='" + metodoPagamento + '\'' +
        '}';
  }

}
