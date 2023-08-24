package com.destiny.repository;

import com.destiny.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface UsuarioRepository extends JpaRepository<Usuario,Long> {

    Usuario findByEmail(String email);
    Usuario findByCpf(String cpf);
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByCpfAndIdNot(String cpf, Long id);

    public interface UsuarioResumo {
        Long getId();
        String getNome();
        String getCpf();
        String getEmail();
        Byte statusConta();
        int tipoConta();

    }

    @Query("SELECT new map(u.id as id, u.nome as nome, u.email as email, u.tipoConta as tipoConta, u.statusConta as statusConta ) FROM Usuario u")
    List<Map<String, Object>> findAllCustom();


}
