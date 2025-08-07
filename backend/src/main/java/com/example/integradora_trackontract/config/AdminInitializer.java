package com.example.integradora_trackontract.config;

import com.example.integradora_trackontract.modules.Roles.model.Roles;
import com.example.integradora_trackontract.modules.Roles.model.RolesRepository;
import com.example.integradora_trackontract.modules.User.model.User;
import com.example.integradora_trackontract.modules.User.model.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AdminInitializer implements ApplicationListener<ContextRefreshedEvent> {
    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;

    private boolean alreadySetup = false;

    public AdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder, RolesRepository rolesRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.rolesRepository = rolesRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (alreadySetup) return;

        String adminEmail = "admin@example.com";

        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            Roles adminRole = rolesRepository.findByName("ADMIN")
                    .orElseGet(() -> {
                        Roles newRole = new Roles();
                        newRole.setName("ADMIN");
                        newRole.setDescription("Administrador del sistema");
                        return rolesRepository.save(newRole);
                    });

            User adminUser = new User();
            adminUser.setName("Admin");
            adminUser.setLastName("Root");
            adminUser.setEmail(adminEmail);
            adminUser.setPhoneNumber("5551234567");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setStatus(true);
            adminUser.setCreated_at(LocalDateTime.now());
            adminUser.setUpdated_at(LocalDateTime.now());
            adminUser.setLogin_attempts(0);
            adminUser.setRol_id(adminRole);

            userRepository.save(adminUser);

            System.out.println("Usuario administrador creado con éxito");
        } else {
            System.out.println("Usuario administrador ya existe");
        }

        alreadySetup = true;
    }
}