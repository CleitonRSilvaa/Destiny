package com.destiny.repository;

import com.destiny.model.Pedido;
import com.destiny.model.Pedido.StatusPedido;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByStatusPedido(StatusPedido status);

    List<Pedido> findByClienteId(Long clienteId);

    List<Pedido> findByClienteNome(String nome);

}
