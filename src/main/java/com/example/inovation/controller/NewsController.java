package com.example.inovation.controller;

import com.example.inovation.controller.form.LinkRequestDto;
import com.example.inovation.controller.form.NewsSearchForm;
import com.example.inovation.service.NewsService;
import com.example.inovation.service.form.ArticleForm;
import com.example.inovation.service.form.NewsForm;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000") //CORS ERROR 해결
public class NewsController {

    private final NewsService newsService;

/*@PostMapping(value = "/search", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE}) //음성(bytearray)으로 키워드 검색
public ResponseEntity<List<ArticleForm>> newsSearch(@RequestPart MultipartFile file) throws Exception {
    //Base64 디코딩을 통해 바이트 배열로 변환
    byte[] word = Base64.getDecoder().decode(file.getBytes());

    //음성을 텍스트로 변환
    //String keyword = newsService.speechKeyword(file.getBytes());
    String keyword = newsService.speechKeyword(word);

    System.out.println(keyword);
    List<ArticleForm> newsList = newsService.searchNaverNews(keyword, 10);
    if (!newsList.isEmpty()) {
        return ResponseEntity.ok(newsList);
    } else {
        System.out.println("결과가 없습니다.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
}*/


    @GetMapping("/search")  //키워드 검색
    public ResponseEntity<List<ArticleForm>> v1newsSearch(@RequestBody NewsSearchForm form) throws Exception {

        List<ArticleForm> newsList = newsService.searchNaverNews(form.getKeyword(), 20);
        if (!newsList.isEmpty()) {
            return ResponseEntity.ok(newsList);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/popularnews") //인기뉴스
    public ResponseEntity<List<NewsForm>> popularNews() throws IOException {

        return ResponseEntity.ok(newsService.getPopularNews());
    }

    @GetMapping("/articles")
    public ResponseEntity<byte[]> receiveArticle(@RequestBody LinkRequestDto body) {
        String link = body.getLink();

        System.out.println(link);
        // link가 비어있는 확인
        if (link.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // link가 제대로 구성되어 있는지 확인
        if (!link.startsWith("http://") && !link.startsWith("https://")) {
            link = "https://" + link;
        }

        try {

            String text = newsService.crawlNewsBody(link);
            byte[] sound = newsService.synthesizeText(text);

            // 오디오를 바이트 배열로 반환
            //return new ResponseEntity<>(sound, headers, HttpStatus.OK);
            return ResponseEntity.ok(sound);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}