package com.imperiumforum.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "role", nullable = false, length = 20)
    private String role = "USER";

    @Column(name = "ban_status", length = 20)
    private String banStatus = "NONE";

    @Column(name = "ban_reason", columnDefinition = "TEXT")
    private String banReason;

    @Column(name = "ban_expires_at")
    private LocalDateTime banExpiresAt;

    @Column(name = "reputation_score", nullable = false)
    private Integer reputationScore = 0;

    @Column(name = "post_count", nullable = false)
    private Integer postCount = 0;

    @Column(name = "topic_count", nullable = false)
    private Integer topicCount = 0;

    @Column(name = "avatar_extension", length = 5)
    private String avatarExtension = ".png";

    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate = LocalDateTime.now();

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Поле для временного хранения уровня репутации (не сохраняется в БД)
    @Transient
    private String reputationLevel;

    // Конструкторы
    public User() {
    }

    public User(String username, String email, String passwordHash) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    // Методы Spring Security UserDetails

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isActive;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Проверяем, не забанен ли пользователь
        if ("PERMA_BAN".equals(banStatus)) {
            return false; // Перманентный бан
        }
        if ("TEMP_BAN".equals(banStatus) && banExpiresAt != null) {
            return LocalDateTime.now().isAfter(banExpiresAt); // Временный бан истек?
        }
        return true; // Нет бана
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Пароль никогда не истекает
    }

    @Override
    public boolean isEnabled() {
        return isActive; // Активен ли аккаунт
    }

    // Метод для проверки, забанен ли пользователь сейчас
    public boolean isCurrentlyBanned() {
        return !isAccountNonLocked();
    }

    // Метод для получения уровня репутации (рассчитывается на лету)
    public String getReputationLevel() {
        if (reputationLevel == null) {
            reputationLevel = calculateReputationLevel(reputationScore);
        }
        return reputationLevel;
    }

    // Метод для расчета уровня репутации
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

    // Метод для получения URL аватара
    public String getAvatarUrl() {
        // Если нет ID или расширения - дефолтный аватар из images
        if (id == null || avatarExtension == null || avatarExtension.trim().isEmpty()) {
            return "/images/default-avatar.png";
        }

        // Формируем путь к пользовательскому аватару
        String filename = id + avatarExtension;

        // Проверяем существование файла в правильной директории
        String userAvatarPath = "src/main/resources/static/uploads/avatars/" + filename;

        try {
            java.nio.file.Path avatarPath = java.nio.file.Paths.get(userAvatarPath);
            if (java.nio.file.Files.exists(avatarPath)) {
                // Файл существует - возвращаем путь к пользовательскому аватару
                return "/uploads/avatars/" + filename + "?v=" + System.currentTimeMillis();
            }
        } catch (Exception e) {
            // В случае ошибки возвращаем дефолтный
            System.err.println("Ошибка при проверке аватара пользователя " + id + ": " + e.getMessage());
        }

        // Файл не существует - возвращаем дефолтный
        return "/images/default-avatar.png";
    }

    // Геттеры и сеттеры

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getBanStatus() {
        return banStatus;
    }

    public void setBanStatus(String banStatus) {
        this.banStatus = banStatus;
    }

    public String getBanReason() {
        return banReason;
    }

    public void setBanReason(String banReason) {
        this.banReason = banReason;
    }

    public LocalDateTime getBanExpiresAt() {
        return banExpiresAt;
    }

    public void setBanExpiresAt(LocalDateTime banExpiresAt) {
        this.banExpiresAt = banExpiresAt;
    }

    public Integer getReputationScore() {
        return reputationScore;
    }

    public void setReputationScore(Integer reputationScore) {
        this.reputationScore = reputationScore;
        // Сбрасываем кэшированный уровень при изменении репутации
        this.reputationLevel = null;
    }

    public Integer getPostCount() {
        return postCount;
    }

    public void setPostCount(Integer postCount) {
        this.postCount = postCount;
    }

    public Integer getTopicCount() {
        return topicCount;
    }

    public void setTopicCount(Integer topicCount) {
        this.topicCount = topicCount;
    }

    public String getAvatarExtension() {
        return avatarExtension;
    }

    public void setAvatarExtension(String avatarExtension) {
        this.avatarExtension = avatarExtension;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public void setReputationLevel(String reputationLevel) {
        this.reputationLevel = reputationLevel;
    }

    // Методы для увеличения счетчиков
    public void incrementPostCount() {
        this.postCount = (this.postCount == null) ? 1 : this.postCount + 1;
    }

    public void incrementTopicCount() {
        this.topicCount = (this.topicCount == null) ? 1 : this.topicCount + 1;
    }

    public void addReputation(int points) {
        this.reputationScore = (this.reputationScore == null) ? points : this.reputationScore + points;
        this.reputationLevel = null; // Сбрасываем кэш уровня
    }

    // Метод для удобного отображения даты регистрации
    public String getFormattedRegistrationDate() {
        if (registrationDate == null) return "";
        return registrationDate.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    // Метод для удобного отображения даты последнего входа
    public String getFormattedLastLogin() {
        if (lastLogin == null) return "Никогда";
        return lastLogin.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", reputationScore=" + reputationScore +
                '}';
    }
}