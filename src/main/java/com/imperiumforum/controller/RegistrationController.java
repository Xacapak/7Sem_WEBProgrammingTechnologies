package com.imperiumforum.controller;

import com.imperiumforum.dto.UserRegistrationDto;
import com.imperiumforum.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserRegistrationDto registrationDto,
                               BindingResult result,
                               Model model) {

        // Проверка ошибок валидации
        if (result.hasErrors()) {
            return "register";
        }

        try {
            // Регистрация пользователя
            registrationService.registerUser(registrationDto);

            // После успешной регистрации - перенаправляем на страницу входа
            model.addAttribute("successMessage", "Регистрация успешна! Теперь вы можете войти.");
            return "redirect:/login?registered=true";

        } catch (IllegalArgumentException e) {
            // Обработка бизнес-ошибок (дубликаты и т.д.)
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
    }
}