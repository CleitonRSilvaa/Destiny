package com.destiny.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
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

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Pedido {
  private static final Random random = new Random();

  public static enum StatusPedido {
    AGUARDANDO_PAGAMENTO("Aguardando Pagamento"),
    PAGEMTNO_REJEITADO("Pagamento Rejeitado"),
    APROVADO("Pagamento com sucesso"),
    CANCELADO("Cancelado"),
    RETIRADA("Aguardando Retirada"),
    TRANSITO("Em Transito"),
    ENTREGUE("Entregue");

    private final String descricao;

    StatusPedido(String descricao) {
      this.descricao = descricao;
    }

    public String getDescricao() {
      return descricao;
    }
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private int numeroPedido;

  @Column(nullable = false)
  private long enderecoEntregaId;

  @ManyToOne
  @JoinColumn(name = "cliente_id", nullable = false)
  private Cliente cliente;

  @Column(nullable = false)
  private StatusPedido statusPedido;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private LocalDate dataPedido;

  @Column(nullable = false)
  private String metodoPagamento;

  @Column(nullable = false)
  private int parcelas;

  @Column(nullable = false)
  private BigDecimal valorTotal;

  // @Column(nullable = false)
  private BigDecimal valorFrete;

  @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
  private List<PedidoDetalhe> itemsPedido;

  public int gerarNumeroUnico5Digitos() {
    return 10000 + random.nextInt(90000);
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
        ", valorTotal='" + valorTotal + '\'' +
        ", itemsPedido='" + itemsPedido + '\'' +
        '}';
  }

}
