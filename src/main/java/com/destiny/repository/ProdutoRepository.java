package com.destiny.repository;

import com.destiny.model.Produto;
import com.destiny.model.StatusConta;
import com.destiny.model.StatusProduto;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    Page<Produto> findByNomeContaining(String nome, Pageable pageable);

    @Transactional
    @Modifying
    @Query("UPDATE Produto p SET p.statusProduto = ?1 WHERE p.id = ?2")
    void updateStatusProduto(StatusProduto statusProduto, Long id);

    @Transactional
    @Modifying
    @Query("UPDATE Produto p SET p.quantidade = ?1 WHERE p.id = ?2")
    void updateQuantidadeProduto(int quantidade, Long id);

    @Query("SELECT p FROM Produto p WHERE p.statusProduto = ?1 and p.quantidade >=1")
    List<Produto> findAllByStatusProduto(StatusProduto status);

}
