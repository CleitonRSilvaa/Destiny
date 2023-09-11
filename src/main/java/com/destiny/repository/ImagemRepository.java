package com.destiny.repository;

import com.destiny.model.Imagem;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ImagemRepository extends JpaRepository<Imagem, Long> {
    List<Imagem> findImageById(long produto_id);
}