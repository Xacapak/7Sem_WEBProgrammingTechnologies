package com.imperiumforum.service;

import com.imperiumforum.dto.UserRegistrationDto;
import com.imperiumforum.entity.User;
import com.imperiumforum.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(UserRegistrationDto registrationDto) {
        // Проверка совпадения паролей
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            throw new IllegalArgumentException("Пароли не совпадают");
        }

        // Проверка существования пользователя
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new IllegalArgumentException("Имя пользователя уже занято");
        }

        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new IllegalArgumentException("Email уже используется");
        }

        // Создание нового пользователя
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());

        // ХЕШИРОВАНИЕ ПАРОЛЯ здесь!
        String hashedPassword = passwordEncoder.encode(registrationDto.getPassword());
        user.setPasswordHash(hashedPassword);

        user.setRole("USER"); // По умолчанию обычный пользователь

        // Сохранение в БД
        return userRepository.save(user);
    }
}