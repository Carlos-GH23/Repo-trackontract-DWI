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

        // ADMIN
        createUserIfNotExists(
                "admin@example.com",
                "Admin",
                "Root",
                "5551234567",
                "admin123",
                "ADMIN",
                "Administrador del sistema"
        );

        // ABOGADO
        createUserIfNotExists(
                "lawyer@example.com",
                "Laura",
                "Justice",
                "5551112222",
                "lawyer123",
                "ABOGADO",
                "Abogado del sistema"
        );

        // CLIENT
        createUserIfNotExists(
                "client@example.com",
                "Carlos",
                "Client",
                "5553334444",
                "client123",
                "CLIENT",
                "Cliente del sistema"
        );

        alreadySetup = true;
    }

    private void createUserIfNotExists(String email, String name, String lastName, String phone, String rawPassword, String roleName, String roleDesc) {
        if (userRepository.findByEmail(email).isPresent()) {
            System.out.println("Usuario ya existe: " + email);
            return;
        }

        Roles role = rolesRepository.findByName(roleName)
                .orElseGet(() -> {
                    Roles newRole = new Roles();
                    newRole.setName(roleName);
                    newRole.setDescription(roleDesc);
                    return rolesRepository.save(newRole);
                });

        User user = new User();
        user.setName(name);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhoneNumber(phone);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setStatus(true);
        user.setCreated_at(LocalDateTime.now());
        user.setUpdated_at(LocalDateTime.now());
        user.setLogin_attempts(0);
        user.setRol_id(role);

        userRepository.save(user);
        System.out.println("Usuario creado: " + email);
    }
}
