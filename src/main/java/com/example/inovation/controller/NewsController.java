package com.example.inovation.controller;

import com.example.inovation.domain.News;
import com.example.inovation.service.NewsService;
import com.example.inovation.service.form.NewsForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @GetMapping("/news")
    public List<NewsForm> news() throws Exception{
        return newsService.NaverNewsCrawler();
    }
}
