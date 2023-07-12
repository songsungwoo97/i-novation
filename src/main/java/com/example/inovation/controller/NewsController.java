package com.example.inovation.controller;

import com.example.inovation.controller.form.NewsSearchForm;
import com.example.inovation.service.NewsService;
import com.example.inovation.service.form.ArticleForm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
<<<<<<< HEAD
import org.springframework.web.multipart.MultipartFile;
=======
>>>>>>> origin/master

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000") //CORS ERROR 해결
public class NewsController {

    private final NewsService newsService;

    @PostMapping("/search")
<<<<<<< HEAD
    public ResponseEntity<List<ArticleForm>> newsSearch(@RequestParam("file") MultipartFile file) throws IOException {
        //STT 추가하기

        //음성을 텍스트로 변환
        String keword = newsService.speechKeyword(file);

        List<ArticleForm> newsList = newsService.searchNaverNews(keword, 20);
=======
    public ResponseEntity<List<ArticleForm>> newsSearch(@RequestBody NewsSearchForm form) throws IOException {
        List<ArticleForm> newsList = newsService.searchNaverNews(form.getKeyword(), 20);
>>>>>>> origin/master
        if (!newsList.isEmpty()) {
            return ResponseEntity.ok(newsList);
        } else {
            System.out.println("결과가 없습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/articles")
    public ResponseEntity<byte[]> receiveArticle(@RequestBody Map<String, String> requestBody) {
        String link = requestBody.get("link"); //

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

            //HttpHeaders headers = new HttpHeaders(); //응답의 헤더 설정(HTTP 헤더는 요청 또는 응답에 대한 추가 컨텍스트 및 메타데이터를 제공)
            //headers.setContentType(MediaType.APPLICATION_OCTET_STREAM); //응답이 바이너리 데이터임을 명시
            //headers.setContentDispositionFormData("attachment", "sound.mp3"); //

            // 오디오를 바이트 배열로 반환
            //return new ResponseEntity<>(sound, headers, HttpStatus.OK);
            return ResponseEntity.ok(sound);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}

