package com.imperiumforum.controller;

import com.imperiumforum.entity.User;
import com.imperiumforum.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TestDataController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public TestDataController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/create-test-users")
    public String createTestUsers(@RequestParam(defaultValue = "false") boolean cleanup) {

        if (cleanup) {
            userRepository.deleteAll();
        }

        // Создаем тестовых пользователей
        createUser("Imperator", "admin@imperium.ru", "admin123", "ADMIN", 10000);
        createUser("Commissar", "moderator@imperium.ru", "moder123", "MODERATOR", 5000);
        createUser("SpaceMarine", "user@imperium.ru", "user123", "USER", 250);

        return "redirect:/login?testUsersCreated=true";
    }

    private void createUser(String username, String email, String password, String role, int reputation) {
        if (!userRepository.existsByUsername(username)) {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPasswordHash(passwordEncoder.encode(password));
            user.setRole(role);
            user.setReputationScore(reputation);
            userRepository.save(user);
            System.out.println("Создан пользователь: " + username + " / " + password);
        }
    }
}