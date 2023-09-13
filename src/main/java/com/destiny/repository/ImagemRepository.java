package com.destiny.repository;

import com.destiny.model.Imagem;
import com.destiny.model.Produto;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ImagemRepository extends JpaRepository<Imagem, Long> {
    List<Imagem> findAllByProduto(Produto produto);

    Optional<Imagem> findById(Long id);

}
