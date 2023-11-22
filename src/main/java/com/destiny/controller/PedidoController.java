package com.destiny.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.destiny.model.Cliente;
import com.destiny.model.CustomUserDetails;
import com.destiny.model.Endereco;
import com.destiny.model.Pedido;
import com.destiny.model.PedidoDTO;
import com.destiny.model.PedidoDetalhe;
import com.destiny.model.Produto;
import com.destiny.repository.ClienteRepository;
import com.destiny.repository.EnderecoRepository;
import com.destiny.repository.PedidoDetalheRepository;
import com.destiny.repository.PedidoRepository;
import com.destiny.repository.ProdutoRepository;

@RestController
@RequestMapping("/pedido")
public class PedidoController {

  private final PedidoRepository pedidoRepository;
  private final PedidoDetalheRepository pedidoDetalheRepository;
  private final ClienteRepository clienteRepository;
  private final EnderecoRepository enderecoRepository;

  @Autowired
  public PedidoController(PedidoRepository pedidoRepository, PedidoDetalheRepository pedidoDetalheRepository,
      ClienteRepository clienteRepository, EnderecoRepository enderecoRepository) {
    this.pedidoRepository = pedidoRepository;
    this.pedidoDetalheRepository = pedidoDetalheRepository;
    this.clienteRepository = clienteRepository;
    this.enderecoRepository = enderecoRepository;
  }

  @Autowired
  private ProdutoRepository produtoRepository;

  // Adicionar um novo pedido
  @PostMapping
  public ResponseEntity<Pedido> createPedido(@RequestBody PedidoDTO pedidodDto) {

    try {
      var pedido = new Pedido();
      var cliente = new Cliente();
      cliente.setId(pedidodDto.getClienteId());
      pedido.setCliente(cliente);
      pedido.setEnderecoEntregaId(pedidodDto.getEnderecoEntregaId());
      pedido.setMetodoPagamento(pedidodDto.getMetodoPagamento());
      pedido.setStatusPedido(Pedido.StatusPedido.AGUARDANDO_PAGAMENTO);
      pedido.setValorTotal(pedidodDto.getValorTotal());
      pedido.setValorFrete(pedidodDto.getValorFrete());
      pedido.setParcelas(pedidodDto.getParcelas());

      Integer numeroPedidoTemp = pedidoRepository.findMaxNumeroPedido();
      System.out.println(numeroPedidoTemp);
      if (numeroPedidoTemp != null) {
        pedido.setNumeroPedido((numeroPedidoTemp + 1));
      } else {
        pedido.setNumeroPedido(pedido.gerarNumeroUnico5Digitos());
      }

      Pedido savedPedido = pedidoRepository.save(pedido);
      for (PedidoDetalhe pd : pedidodDto.getItemsPedido()) {
        pd.setPedido(savedPedido);
        pedidoDetalheRepository.save(pd);
        // Recupera o produto do banco de dados
        Produto produto = produtoRepository.findById(pd.getProdutoId()).orElse(null);

        if (produto != null) {
          produto.setQuantidade(produto.getQuantidade() - pd.getQuantidade());
          produtoRepository.save(produto);
        }

      }

      URI location = URI.create(String.format("/pedido/%s", savedPedido.getId()));

      return ResponseEntity.created(location).body(savedPedido);
    } catch (Exception e) {
      return (ResponseEntity) ResponseEntity.status(404);
    }

  }

  @PostMapping("/2")
  public boolean createPedidoItem(@RequestBody PedidoDetalhe pedido) {

    System.out.println(pedido);
    // Pedido savedPedido = pedidoRepository.save(pedido);
    // URI location = URI.create(String.format("/pedidos/%s", savedPedido.getId()));
    // return ResponseEntity.created(location).body(savedPedido);
    return true;
  }

  // Obter um pedido pelo ID
  @GetMapping("/{id}")
  public ResponseEntity<Pedido> getPedidoById(@PathVariable Long id) {
    Optional<Pedido> pedido = pedidoRepository.findById(id);
    return pedido.map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  // Obter todos os pedidos
  @GetMapping
  public List<Pedido> getAllPedidos() {
    return pedidoRepository.findAll();
  }

}
