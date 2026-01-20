package com.imperiumforum.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Random;

@Controller
public class HomeController {

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
        model.addAttribute("randomPhrase", randomPhrase);
        model.addAttribute("currentYear", java.time.Year.now().getValue());

        return "index";  // Теперь ищет templates/index.html
    }
}