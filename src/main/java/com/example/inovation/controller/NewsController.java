package com.example.inovation.controller;

import com.example.inovation.domain.news.dto.ArticleDto;
import com.example.inovation.domain.news.dto.LinkRequestDto;
import com.example.inovation.domain.news.dto.NewsForm;
import com.example.inovation.domain.news.dto.NewsSearchRequestDto;
import com.example.inovation.domain.news.service.NewsService;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000") //CORS ERROR 해결
public class NewsController {

    private final NewsService newsService;


    /*@PostMapping(value = "/search", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    //음성(bytearray)으로 키워드 검색
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

    /**
     * 키워드로 뉴스검색
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/search")  //키워드 검색
    public ResponseEntity<List<ArticleDto>> searchNewsByKeyword( @RequestParam String keyword,
                                                                 @RequestParam(required = false, defaultValue = "20") int display) throws Exception {

        List<ArticleDto> newsList = newsService.searchNaverNews(keyword, display);
        if (!newsList.isEmpty()) {
            return ResponseEntity.ok(newsList);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * 인기뉴스 목록 조회
     *
     * @return
     * @throws IOException
     */
    @GetMapping("/popularnews") //인기뉴스
    public ResponseEntity<List<NewsForm>> popularNews() throws IOException {

        return ResponseEntity.ok(newsService.getPopularNews());
    }


    /**
     * 뉴스 본문을 음성으로 변환하여 반환
     *
     * @param
     * @return
     */
    @GetMapping("/text-to-speech")
    public ResponseEntity<?> receiveArticle(@RequestBody LinkRequestDto requestDto) {
        String link = requestDto.getLink();

        // link가 비어있는 확인
        if (link == null || link.isEmpty()) {
            return ResponseEntity.badRequest().body("링크가 제공되지 않았습니다.");
        }

        // link가 제대로 구성되어 있는지 확인
        if (!link.startsWith("http://") && !link.startsWith("https://")) {
            link = "https://" + link;
        }

        try {

            String text = newsService.crawlNewsBody(link);
            byte[] sound = newsService.synthesizeText(text);

            return ResponseEntity.ok(sound);

        } catch (IOException e) {
            log.error("음성 변환 오류: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 뉴스 링크를 통해 뉴스 상세 내용 크롤링
     */
    @PostMapping("/content")
    public ResponseEntity<?> getNewsContent(@RequestBody LinkRequestDto requestDto) {
        String link = requestDto.getLink();

        if (link == null || link.isEmpty()) {
            return ResponseEntity.badRequest().body("링크가 제공되지 않았습니다.");
        }

        String newsContent = newsService.crawlNewsBody(link);
        return ResponseEntity.ok(newsContent);
    }
}