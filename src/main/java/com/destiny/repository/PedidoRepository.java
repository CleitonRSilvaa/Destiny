package com.destiny.repository;

import com.destiny.model.Pedido;
import com.destiny.model.Pedido.StatusPedido;
import com.destiny.model.Produto;
import com.destiny.model.StatusConta;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("SELECT MAX(p.numeroPedido) FROM Pedido p")
    Integer findMaxNumeroPedido();

    Page<Pedido> findByNumeroPedidoContaining(String numeroPedido, Pageable pageable);

    @Transactional
    @Modifying
    @Query("UPDATE Pedido p SET p.statusPedido = ?1 WHERE p.id = ?2")
    void updateStatusPedido(Pedido.StatusPedido statusPedido, Long id);

}
