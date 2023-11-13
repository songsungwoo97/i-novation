package com.example.inovation.controller;

import com.example.inovation.controller.form.NewsSearchForm;
import com.example.inovation.controller.form.Search;
import com.example.inovation.service.NewsService;
import com.example.inovation.service.form.ArticleForm;
import com.example.inovation.service.form.NewsForm;
import org.springframework.core.io.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000") //CORS ERROR 해결
public class NewsController {

    private final NewsService newsService;

@PostMapping(value = "/search", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE}) //음성(bytearray)으로 키워드 검색
public ResponseEntity<List<ArticleForm>> newsSearch(@RequestPart MultipartFile file) throws IOException {
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
}


    @PostMapping("/v1-search")  //키워드 검색
    public ResponseEntity<List<ArticleForm>> v1newsSearch(@RequestBody NewsSearchForm form) throws IOException {

        List<ArticleForm> newsList = newsService.searchNaverNews(form.getKeyword(), 10);
        if (!newsList.isEmpty()) {
            return ResponseEntity.ok(newsList);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/popularnews") //인기뉴스
    public ResponseEntity<List<NewsForm>> popularNews() throws IOException {

        return ResponseEntity.ok(newsService.getPopularNews());
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

            // 오디오를 바이트 배열로 반환
            //return new ResponseEntity<>(sound, headers, HttpStatus.OK);
            return ResponseEntity.ok(sound);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //파일로 보내기
//    @PostMapping("/articles")
//    public ResponseEntity<Resource> receiveArticle(@RequestBody Map<String, String> requestBody) {
//        String link = requestBody.get("link"); //
//
//        System.out.println(link);
//        // link가 비어있는 확인
//        if (link.isEmpty()) {
//            return ResponseEntity.badRequest().build();
//        }
//
//        // link가 제대로 구성되어 있는지 확인
//        if (!link.startsWith("http://") && !link.startsWith("https://")) {
//            link = "https://" + link;
//        }
//
//        try {
//
//            String text = newsService.crawlNewsBody(link);
//            byte[] sound = newsService.synthesizeText(text);
//
//            //HttpHeaders headers = new HttpHeaders(); //응답의 헤더 설정(HTTP 헤더는 요청 또는 응답에 대한 추가 컨텍스트 및 메타데이터를 제공)
//            //headers.setContentType(MediaType.APPLICATION_OCTET_STREAM); //응답이 바이너리 데이터임을 명시
//            //headers.setContentDispositionFormData("attachment", "sound.mp3"); //
//
//            Path filePath = Paths.get("/home/ubuntu/mp3/sound.mp3"); //저장할 경로 지정
//
//            Files.write(filePath, sound); //위치에 파일 저장
//
//            Resource resource = new UrlResource(filePath.toUri()); //uri 생성
//
//            return ResponseEntity.ok().body(resource);
//            // 오디오를 바이트 배열로 반환
//            //return new ResponseEntity<>(sound, headers, HttpStatus.OK);
//            //return ResponseEntity.ok(sound);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }

//    @PostMapping("/articles")
//    public ResponseEntity<UrlResource> receiveArticle(@RequestBody Map<String, String> requestBody) throws IOException {
//        String link = requestBody.get("link"); //
//
//        // link가 비어있는 확인
//        if (link.isEmpty()) {
//            return ResponseEntity.badRequest().build();
//        }
//
//        // link가 제대로 구성되어 있는지 확인
//        if (!link.startsWith("http://") && !link.startsWith("https://")) {
//            link = "https://" + link;
//        }
//
//        String text = newsService.crawlNewsBody(link);
//        byte[] sound = newsService.synthesizeText(text);
//
//
//        //Path filePath = Paths.get("/home/ubuntu/mp3/sound.mp3"); //저장할 경로 지정
//        Path filePath = Paths.get("C:/Users/USER_20211214/Desktop/sound/sound.mp3");
//
//        Files.write(filePath, sound); //위치에 파일 저장
//
//        UrlResource resource = new UrlResource(filePath.toUri()); //uri 생성
//        String encodedUploadFileName = UriUtils.encode(resource.getFilename(), StandardCharsets.UTF_8);
//
//        String contentDisposition = "attachment; filename=\"" + encodedUploadFileName + "\"";
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
//                .body(resource);
//    }
}