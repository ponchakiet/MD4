package com.example.identityservice.config;

import com.example.identityservice.entity.User;
import com.example.identityservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("student")) {
            User student = new User();
            student.setUsername("student");
            student.setPassword(passwordEncoder.encode("123456"));
            student.setRole("STUDENT");
            userRepository.save(student);
            log.info("Created student user");
        }

        if (!userRepository.existsByUsername("instructor")) {
            User instructor = new User();
            instructor.setUsername("instructor");
            instructor.setPassword(passwordEncoder.encode("123456"));
            instructor.setRole("INSTRUCTOR");
            userRepository.save(instructor);
            log.info("Created instructor user");
        }
    }
}