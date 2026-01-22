package com.imperiumforum.controller;

import com.imperiumforum.entity.Category;
import com.imperiumforum.repository.CategoryRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Random;

@Controller
public class HomeController {

    private final CategoryRepository categoryRepository;
    public HomeController(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        String[] phrases = {
                "ДОБРО ПОЖАЛОВАТЬ В ЧЕРТОГИ ЗНАНИЯ!",
                "ЗДЕСЬ РОЖДАЮТСЯ ВЕЛИКИЕ ТАКТИКИ!",
                "ВАШИ СЛОВА СТАНУТ ХРОНИКАМИ ВОЙНЫ!",
                "ПОДЕЛИСЬ МУДРОСТЬЮ, СОЛДАТ ИМПЕРИУМА!",
                "В ЭТИХ СТЕНАХ РЕШАЮТСЯ СУДЬБЫ ГАЛАКТИК!",
                "ПИШИ СМЕЛО, ТВОЁ СЛОВО УСЛЫШАТ!",
                "ЗДЕСЬ СОБИРАЮТСЯ ВЕРНЫЕ СЫНЫ ИМПЕРИИ!"
        };

        String randomPhrase = phrases[new Random().nextInt(phrases.length)];

        List<Category> categories = categoryRepository.findByIsActiveTrueOrderByDisplayOrderAsc();

        model.addAttribute("randomPhrase", randomPhrase);
        model.addAttribute("currentYear", java.time.Year.now().getValue());
        model.addAttribute("categories", categories);

        return "index";  // Теперь ищет templates/index.html
    }
}