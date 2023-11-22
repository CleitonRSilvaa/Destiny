package com.destiny.model;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class PedidoDTO {

  @NotNull
  private long enderecoEntregaId;

  @NotNull
  private long clienteId;

  @NotNull
  private String metodoPagamento;

  @NotNull
  private BigDecimal valorTotal;

  @NotNull
  private BigDecimal valorFrete;

  @NotNull
  private int parcelas;

  @NotNull
  private List<PedidoDetalhe> itemsPedido;

  @Override
  public String toString() {
    return "Pedido{" +
        "enderecoEntregaId=" + enderecoEntregaId +
        ", clienteId=" + clienteId +
        ", metodoPagamento='" + metodoPagamento + '\'' +
        '}';
  }

}
