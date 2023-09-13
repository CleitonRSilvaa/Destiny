package com.destiny.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Imagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String caminho;

    private Boolean principal;

    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Override
    public String toString() {
        return "Imagem {id=" + id + ", url=" + caminho + "}";
    }

}