package com.imperiumforum.controller;

import com.imperiumforum.entity.Topic;
import com.imperiumforum.entity.Post;
import com.imperiumforum.entity.User;
import com.imperiumforum.repository.TopicRepository;
import com.imperiumforum.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/topic")
public class TopicController {

    private final TopicRepository topicRepository;
    private final PostRepository postRepository;

    public TopicController(TopicRepository topicRepository,
                           PostRepository postRepository) {
        this.topicRepository = topicRepository;
        this.postRepository = postRepository;
    }

    // Страница темы (сообщения)
    @GetMapping("/{topicId}")
    public String showTopic(@PathVariable Long topicId,
                            @RequestParam(defaultValue = "1") int page,
                            @AuthenticationPrincipal User currentUser,
                            Model model) {

        Optional<Topic> topicOpt = topicRepository.findById(topicId);

        if (topicOpt.isEmpty() || !topicOpt.get().getIsActive()) {
            model.addAttribute("error", "Тема не найдена или удалена");
            return "forum/topic";
        }

        Topic topic = topicOpt.get();

        // Увеличиваем счетчик просмотров
        topic.setViewCount(topic.getViewCount() + 1);
        topicRepository.save(topic);

        // Получаем сообщения с пагинацией
        Pageable pageable = PageRequest.of(page - 1, 10);
        Page<Post> postsPage = postRepository.findByTopicIdAndIsActiveTrueOrderByCreatedAtAsc(topicId, pageable);

        // Получаем общее количество сообщений
        long totalPosts = postRepository.countByTopicIdAndIsActiveTrue(topicId) + 1; // +1 первое сообщение темы

        model.addAttribute("topic", topic);
        model.addAttribute("posts", postsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postsPage.getTotalPages());
        model.addAttribute("totalPosts", totalPosts);

        // Добавляем информацию об авторе для проверки
        if (currentUser != null) {
            model.addAttribute("isAuthor", currentUser.getId().equals(topic.getAuthor().getId()));
        }

        return "forum/topic";
    }

    // Обработка добавления нового сообщения
    @PostMapping("/{topicId}/reply")
    public String addReply(@PathVariable Long topicId,
                           @RequestParam("content") String content,
                           @AuthenticationPrincipal User currentUser) {

        if (currentUser == null) {
            return "redirect:/login";
        }

        Optional<Topic> topicOpt = topicRepository.findById(topicId);

        if (topicOpt.isEmpty() || !topicOpt.get().getIsActive() || topicOpt.get().getIsLocked()) {
            return "redirect:/";
        }

        Topic topic = topicOpt.get();

        // Создаем новое сообщение
        Post newPost = new Post();
        newPost.setContent(content);
        newPost.setAuthor(currentUser);
        newPost.setTopic(topic);
        newPost.setIsActive(true);
        newPost.setCreatedAt(LocalDateTime.now());

        postRepository.save(newPost);

        // Обновляем статистику темы
        topic.setPostCount(topic.getPostCount() + 1);
        topic.setLastPostAt(LocalDateTime.now());
        topic.setLastPostBy(currentUser);
        topic.setUpdatedAt(LocalDateTime.now());
        topicRepository.save(topic);

        // Увеличиваем счетчик сообщений у пользователя
        currentUser.setPostCount(currentUser.getPostCount() + 1);
        // Note: Для обновления пользователя нужно использовать UserRepository

        // Перенаправляем на последнюю страницу темы
        long totalPosts = postRepository.countByTopicIdAndIsActiveTrue(topicId) + 1;
        int lastPage = (int) Math.ceil((double) totalPosts / 10);

        return "redirect:/topic/" + topicId + "?page=" + lastPage + "#post-" + newPost.getId();
    }
}