package com.example.inovation.controller;

import com.example.inovation.domain.News;
import com.example.inovation.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @GetMapping("/news")
    public String news(Model model) throws Exception{
        List<News> newsList = newsService.NaverNewsCrawler();
        model.addAttribute("news", newsList);

        return "news";
    }
}
