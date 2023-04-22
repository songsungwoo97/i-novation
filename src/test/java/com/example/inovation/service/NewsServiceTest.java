package com.example.inovation.service;

import com.example.inovation.domain.News;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class NewsServiceTest {

    @Autowired
    private NewsService newsService;

    @Test
    public void crawling() throws IOException {
        List<News> newsList = newsService.NaverNewsCrawler();

        System.out.println(newsList.size());
    }
}