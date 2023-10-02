package com.example.inovation.controller;

import com.example.inovation.controller.form.NewsSearchForm;
import com.example.inovation.service.NewsService;
import com.example.inovation.service.form.ArticleForm;
import com.example.inovation.service.form.NewsForm;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000") //CORS ERROR 해결
public class NewsController {

    private final NewsService newsService;

/*    @PostMapping("/search")
    public ResponseEntity<List<ArticleForm>> newsSearch(@RequestParam("file") MultipartFile file) throws IOException {
        //STT 추가하기

        //음성을 텍스트로 변환
        String keword = newsService.speechKeyword(file);

        List<ArticleForm> newsList = newsService.searchNaverNews(keword, 20);
        if (!newsList.isEmpty()) {
            return ResponseEntity.ok(newsList);
        } else {
            System.out.println("결과가 없습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
*/
    @PostMapping("/popularnews")
    public ResponseEntity<List<NewsForm>> popularNews() throws IOException {

        System.out.println("1");

        return ResponseEntity.ok(newsService.getPopularNews());
    }

    @PostMapping("/search")
    public ResponseEntity<List<ArticleForm>> newsSearch(@RequestBody NewsSearchForm from) throws IOException {

        List<ArticleForm> newsList = newsService.searchNaverNews(from.getKeyword(), 20);
        if (!newsList.isEmpty()) {
            return ResponseEntity.ok(newsList);
        } else {
            System.out.println("결과가 없습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


//    @PostMapping("/articles")
//    public ResponseEntity<byte[]> receiveArticle(@RequestBody Map<String, String> requestBody) {
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
//            // 오디오를 바이트 배열로 반환
//            //return new ResponseEntity<>(sound, headers, HttpStatus.OK);
//            return ResponseEntity.ok(sound);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }

    //파일로 보내기
    @PostMapping("/articles")
    public ResponseEntity<Resource> receiveArticle(@RequestBody Map<String, String> requestBody) {
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

            Path filePath = Paths.get("/home/ubuntu/mp3/sound.mp3"); //저장할 경로 지정

            Files.write(filePath, sound); //위치에 파일 저장

            Resource resource = new UrlResource(filePath.toUri()); //uri 생성

            return ResponseEntity.ok().body(resource);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}

