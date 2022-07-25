package com.example.cinema;

import com.example.cinema.model.entities.user.Role;
import com.example.cinema.model.entities.user.User;
import com.example.cinema.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@PropertySource("classpath:security.properties")
@SpringBootApplication
public class CinemaApplication {

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    public static void main(String[] args) {
        SpringApplication.run(CinemaApplication.class, args);
    }

    @Bean
    @Profile("!test")
    protected CommandLineRunner commandLineRunner(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        return args -> {
            saveAdmin(passwordEncoder, userRepository);
        };
    }

    private void saveAdmin(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        User admin = getDefaultUser();
        admin.setEnabled(true);

        String encodedPassword = passwordEncoder.encode(adminPassword);
        admin.setPassword(encodedPassword);

        Optional<User> possibleUser = userRepository.findByEmail(admin.getEmail());
        if (possibleUser.isEmpty()) {
            userRepository.save(admin);
            return;
        }

        User existing = possibleUser.get();
        if(!passwordEncoder.matches(admin.getPassword(), existing.getPassword())){
            admin.setId(existing.getId());
            userRepository.save(admin);
        }
    }

    private User getDefaultUser() {
        User admin = new User();
        
        admin.setFirstName("Admin");
        admin.setLastName("Admin");
        admin.setEmail(adminEmail);
        admin.setRole(Role.ADMIN);

        return admin;
    }
}
