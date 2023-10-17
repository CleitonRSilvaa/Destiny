package com.destiny.repository;

import com.destiny.model.StatusConta;
import com.destiny.model.TipoConta;
import com.destiny.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Usuario findByEmail(String email);

    Usuario findByCpf(String cpf);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByCpfAndIdNot(String cpf, Long id);

    @Query("SELECT u FROM Usuario u WHERE LOWER(u.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<UsuarioResumo> findByNomeContainingIgnoreCase(@Param("nome") String nome);

    public interface UsuarioResumo {
        Long getId();

        String getCpf();

        String getEmail();

        String getNome();

        StatusConta getStatusConta();

        TipoConta getTipoConta();

    }

    @Query("select u.id as id, u.cpf as cpf, u.email as email, u.nome as nome, u.statusConta as statusConta, u.tipoConta as tipoConta from Usuario u")
    List<UsuarioResumo> findAllCustom();

    @Transactional
    @Modifying
    @Query("UPDATE Usuario u SET u.statusConta = ?1 WHERE u.id = ?2")
    void updateStatusConta(StatusConta statusConta, Long id);

}
