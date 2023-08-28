package com.destiny.model;


import com.destiny.utils.EncriptSenha;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String nome;

    @Column(unique = true, nullable = false, updatable = false)
    private String email;

    @Column(nullable = false,unique = true)
    private String cpf;
    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoConta tipoConta;


    @Column(nullable = false)
    private StatusConta statusConta ;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Date dataCriacao;

    public void setSenha(String senha) {
        this.senha = EncriptSenha.md5(senha);
    }

    public void setNome(String nome) {
        this.nome = nome.toUpperCase();
    }
}
