package com.example.inovation.controller;

import com.example.inovation.controller.form.NewsSearchForm;
import com.example.inovation.domain.News;
import com.example.inovation.service.NewsService;
import com.example.inovation.service.form.NewsForm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000") //CORS ERROR 해결
public class NewsController {

    private final NewsService newsService;

    //웹크롤링
    @GetMapping("/crawling")
    public List<String> newsCrawling() throws Exception{
        return newsService.naverNewsCrawler();
    }

    //뉴스 기사 검색
    @GetMapping("/search")
    public ResponseEntity<List<String>> newsSearch(@RequestBody NewsSearchForm form) {
        List<String> newsList = newsService.search(form.getKeyword());
        if (newsList != null && !newsList.isEmpty()) {
            return ResponseEntity.ok(newsList);
        } else {
            System.out.println("결과가 없습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    //ObjectMapper objectMapper = new ObjectMapper();
    /*@GetMapping("/search")
    public List<String> newsSearch(@RequestParam("keyword") String keyword) throws Exception {
        JsonNode jsonNode = objectMapper.readTree(keyword);
        String result = jsonNode.get("keyword").asText();
        return newsService.search(result);
    }*/
}
