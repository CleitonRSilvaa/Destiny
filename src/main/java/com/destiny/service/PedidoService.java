package com.destiny.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.destiny.model.Pedido;
import com.destiny.model.Produto;
import com.destiny.repository.ClienteRepository;
import com.destiny.repository.EnderecoRepository;
import com.destiny.repository.PedidoDetalheRepository;
import com.destiny.repository.PedidoRepository;

@Service
public class PedidoService {

  @Autowired
  private PedidoRepository pedidoRepository;

  @Autowired
  private PedidoDetalheRepository pedidoDetalheRepository;
  @Autowired
  private ClienteRepository clienteRepository;
  @Autowired
  private EnderecoRepository enderecoRepository;

  public Page<Pedido> buscarPedidos(Optional<Integer> page, Optional<Integer> size, Optional<Integer> numeroPedido) {
    int currentPage = page.orElse(1) - 1;
    int pageSize = size.orElse(10);

    PageRequest pageRequest = PageRequest.of(currentPage, pageSize, Sort.by("id").descending());

    if (numeroPedido.isPresent()) {

      System.out.println(numeroPedido.get().intValue());
      System.out.println(numeroPedido.get());
      return pedidoRepository.findByNumeroPedidoContaining(numeroPedido.get().toString(), pageRequest);
    } else {
      return pedidoRepository.findAll(pageRequest);
    }
  }

  public boolean valiadePedido(long id) {
    return pedidoRepository.existsById(id);
  }

  public boolean updateStatusPedido(Pedido.StatusPedido statusPedido, Long id) {

    try {
      pedidoRepository.updateStatusPedido(statusPedido, id);
      return true;
    } catch (Exception e) {
      return false;
    }

  }

}
