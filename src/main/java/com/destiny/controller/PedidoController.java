package com.destiny.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.destiny.model.Cliente;
import com.destiny.model.Pedido;
import com.destiny.model.PedidoDTO;
import com.destiny.model.PedidoDetalhe;
import com.destiny.repository.PedidoDetalheRepository;
import com.destiny.repository.PedidoRepository;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

  private final PedidoRepository pedidoRepository;
  private final PedidoDetalheRepository pedidoDetalheRepository;

  @Autowired
  public PedidoController(PedidoRepository pedidoRepository, PedidoDetalheRepository pedidoDetalheRepository) {
    this.pedidoRepository = pedidoRepository;
    this.pedidoDetalheRepository = pedidoDetalheRepository;
  }

  // Adicionar um novo pedido
  @PostMapping
  public ResponseEntity<Pedido> createPedido(@RequestBody PedidoDTO pedidodDto) {

    var pedido = new Pedido();
    var cliente = new Cliente();
    cliente.setId(pedidodDto.getClienteId());
    pedido.setCliente(cliente);
    pedido.setEnderecoEntregaId(pedidodDto.getEnderecoEntregaId());
    pedido.setMetodoPagamento(pedidodDto.getMetodoPagamento());
    pedido.setStatusPedido(Pedido.StatusPedido.PENDENTE);
    // pedido.setItemsPedido();
    pedido.setValorTotal(pedidodDto.getValorTotal());
    pedido.setNumeroPedido();

    System.out.println(pedido.toString());

    Pedido savedPedido = pedidoRepository.save(pedido);
    for (PedidoDetalhe pd : pedidodDto.getItemsPedido()) {
      pedidoDetalheRepository.save(pd.setPedido(savedPedido))
    }
    URI location = URI.create(String.format("/pedidos/%s", savedPedido.getId()));

    return ResponseEntity.created(location).body(savedPedido);

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
