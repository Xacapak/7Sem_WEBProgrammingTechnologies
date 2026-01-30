package com.imperiumforum.controller;

import com.imperiumforum.entity.User;
import com.imperiumforum.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Duration;
import java.time.LocalDateTime;

@Controller
public class ProfileController {

    private final UserRepository userRepository;

    public ProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal User currentUser, Model model) {
        // Получаем актуальные данные пользователя
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Рассчитываем уровень репутации
        String reputationLevel = calculateReputationLevel(user.getReputationScore());
        user.setReputationLevel(reputationLevel);

        // Добавляем дополнительные атрибуты
        model.addAttribute("user", user);
        model.addAttribute("isOwnProfile", true);
        model.addAttribute("activityLevel", calculateActivityLevel(user));
        model.addAttribute("joinDuration", calculateJoinDuration(user));
        model.addAttribute("rankColor", getRankColor(reputationLevel));
        model.addAttribute("roleIcon", getRoleIcon(user.getRole()));
        model.addAttribute("roleIcon", getRoleIcon(user.getRole())); // дублирование для разных мест

        return "profile";
    }

    private String calculateReputationLevel(int score) {
        if (score >= 10000) return "Примарх";
        if (score >= 5000) return "Инквизитор";
        if (score >= 1000) return "Комиссар";
        if (score >= 500) return "Сержант";
        if (score >= 100) return "Боец стражи";
        if (score >= 0) return "Неофит";
        if (score >= -100) return "Сомнительный";
        if (score >= -500) return "Отступник";
        return "Еретик";
    }

    private String calculateActivityLevel(User user) {
        int totalActivity = user.getPostCount() + user.getTopicCount();

        if (totalActivity >= 1000) return "Легенда";
        if (totalActivity >= 500) return "Ветеран";
        if (totalActivity >= 100) return "Активный";
        if (totalActivity >= 50) return "Регулярный";
        if (totalActivity >= 10) return "Новичок";
        return "Наблюдатель";
    }

    private String calculateJoinDuration(User user) {
        LocalDateTime joinDate = user.getRegistrationDate();
        if (joinDate == null) return "Неизвестно";

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(joinDate, now);
        long days = duration.toDays();

        if (days < 1) return "Сегодня";
        if (days == 1) return "1 день";
        if (days < 30) return days + " дней";
        if (days < 365) {
            long months = days / 30;
            return months + (months == 1 ? " месяц" : " месяцев");
        }
        long years = days / 365;
        return years + (years == 1 ? " год" : " лет");
    }

    private String getRankColor(String reputationLevel) {
        if (reputationLevel == null) return "#cccccc";

        return switch (reputationLevel) {
            case "Примарх" -> "#ffd700";
            case "Инквизитор" -> "#c0c0c0";
            case "Комиссар" -> "#cd7f32";
            case "Сержант" -> "#00aa00";
            case "Боец стражи" -> "#0088ff";
            case "Неофит" -> "#aaaaaa";
            case "Сомнительный" -> "#ff8800";
            case "Отступник" -> "#ff4444";
            case "Еретик" -> "#880000";
            default -> "#cccccc";
        };
    }

    private String getRoleIcon(String role) {
        if (role == null) return "⚔️";

        return switch (role.toUpperCase()) {
            case "ADMIN" -> "👑";
            case "MODERATOR" -> "⭐";
            default -> "⚔️";
        };
    }
}