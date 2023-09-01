package com.destiny.component;

import com.destiny.model.StatusConta;
import com.destiny.model.TipoConta;
import com.destiny.model.Usuario;
import com.destiny.repository.UsuarioRepository;

import ch.qos.logback.classic.Logger;

import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public void run(String... args) {

        if (usuarioRepository.findByEmail("admin.master@destiny.com") == null) {
            Usuario admin = new Usuario();
            admin.setNome("Admin User");
            admin.setEmail("admin.master@destiny.com");
            admin.setCpf("99999999999");
            admin.setSenha("Destiny@2023");
            admin.setTipoConta(TipoConta.ADMIN);
            admin.setStatusConta(StatusConta.ATIVA);
            usuarioRepository.save(admin);
        }
    }

    private Logger LoggerFactory(Class<DataInitializer> class1) {
        return null;
    }
}
