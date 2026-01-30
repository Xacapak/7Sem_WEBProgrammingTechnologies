package com.imperiumforum.controller;

import com.imperiumforum.entity.Category;
import com.imperiumforum.entity.Topic;
import com.imperiumforum.entity.User;
import com.imperiumforum.repository.CategoryRepository;
import com.imperiumforum.repository.TopicRepository;
import com.imperiumforum.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/forum")
public class ForumController {

    private final CategoryRepository categoryRepository;
    private final TopicRepository topicRepository;
    private final PostRepository postRepository;

    public ForumController(CategoryRepository categoryRepository,
                           TopicRepository topicRepository,
                           PostRepository postRepository) {
        this.categoryRepository = categoryRepository;
        this.topicRepository = topicRepository;
        this.postRepository = postRepository;
    }

    // Страница раздела (список тем)
    @GetMapping("/{categoryId}")
    public String showCategory(@PathVariable Long categoryId,
                               @RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "20") int size,
                               Model model) {

        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);

        if (categoryOpt.isEmpty() || !categoryOpt.get().getIsActive()) {
            return "redirect:/";
        }

        Category category = categoryOpt.get();

        // Получаем закрепленные темы
        List<Topic> pinnedTopics = topicRepository.findByCategoryIdAndIsPinnedTrueAndIsActiveTrueOrderByCreatedAtDesc(categoryId);

        // Получаем обычные темы с пагинацией
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<Topic> topicsPage = topicRepository.findByCategoryIdAndIsActiveTrueOrderByIsPinnedDescCreatedAtDesc(categoryId, pageable);

        // Подсчитываем количество тем и сообщений в разделе
        long totalTopics = topicRepository.countByCategoryIdAndIsActiveTrue(categoryId);
        long totalPosts = postRepository.countByTopicCategoryIdAndIsActiveTrue(categoryId);

        model.addAttribute("category", category);
        model.addAttribute("pinnedTopics", pinnedTopics);
        model.addAttribute("topics", topicsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", topicsPage.getTotalPages());
        model.addAttribute("totalTopics", totalTopics);
        model.addAttribute("totalPosts", totalPosts);

//        return "forum/category";
        return "category";
    }

    // Страница создания новой темы
    @GetMapping("/{categoryId}/new-topic")
    public String showNewTopicForm(@PathVariable Long categoryId,
                                   @AuthenticationPrincipal User currentUser,
                                   Model model) {

        if (currentUser == null) {
            return "redirect:/login";
        }

        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);

        if (categoryOpt.isEmpty() || !categoryOpt.get().getIsActive()) {
            return "redirect:/";
        }

        model.addAttribute("category", categoryOpt.get());
        model.addAttribute("topic", new Topic());

        return "forum/new-topic";
    }

    // Обработка создания новой темы
    @PostMapping("/{categoryId}/new-topic")
    public String createNewTopic(@PathVariable Long categoryId,
                                 @ModelAttribute Topic topic,
                                 @AuthenticationPrincipal User currentUser,
                                 Model model) {

        if (currentUser == null) {
            return "redirect:/login";
        }

        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);

        if (categoryOpt.isEmpty() || !categoryOpt.get().getIsActive()) {
            return "redirect:/";
        }

        // Устанавливаем автора и раздел
        topic.setAuthor(currentUser);
        topic.setCategory(categoryOpt.get());
        topic.setIsActive(true);
        topic.setIsPinned(false);
        topic.setIsLocked(false);
        topic.setViewCount(0);
        topic.setPostCount(0);

        // Сохраняем тему
        Topic savedTopic = topicRepository.save(topic);

        // Увеличиваем счетчик тем у пользователя
        currentUser.incrementTopicCount();

        // Перенаправляем на созданную тему
        return "redirect:/topic/" + savedTopic.getId();
    }
}