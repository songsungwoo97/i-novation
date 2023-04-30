package com.example.inovation.repository;

import com.example.inovation.domain.News;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class NewsRepositoryTest {

    @Autowired
    private NewsRepository newsRepository;

    @Test
    public void 저장() {
        News news = new News("fsd", "fsdfs", "sdfds");

        newsRepository.save(news);
    }
}