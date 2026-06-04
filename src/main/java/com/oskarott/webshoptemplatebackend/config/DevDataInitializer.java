package com.oskarott.webshoptemplatebackend.config;

import com.oskarott.webshoptemplatebackend.model.Role;
import com.oskarott.webshoptemplatebackend.model.UserEntity;
import com.oskarott.webshoptemplatebackend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
public class DevDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DevDataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.existsByEmail("admin@local.dev")) {
            return;
        }

        UserEntity admin = new UserEntity();
        admin.setEmail("admin@local.dev");
        admin.setFirstName("Admin");
        admin.setLastName("Local");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ADMIN);

        userRepository.save(admin);
    }
}
