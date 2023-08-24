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
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String nome;

    @Column(unique = true)
    private String email;

    @Column(nullable = false,unique = true)
    private String cpf;

    @Column(length = 12)
    private String telefone;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false)
    private Byte statusConta ;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Date dataCriacao;

    public void setSenha(String senha) {
        this.senha = EncriptSenha.md5(senha);
    }
}
