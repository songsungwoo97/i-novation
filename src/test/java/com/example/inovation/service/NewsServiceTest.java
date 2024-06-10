package com.example.inovation.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.speech.v1p1beta1.*;
import com.google.protobuf.ByteString;
import javazoom.jl.player.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class NewsServiceTest {

    @Autowired
    private NewsService newsService;

    @Test
    public void STT_MP3() throws IOException {
        Path path = Paths.get("C:/Users/USER_20211214/Desktop/test.mp3"); // 음성 파일의 경로를 지정합니다.

        String resultText = "";

        try {
            //SpeechClient인스턴스 생성
            SpeechClient speechClient = SpeechClient.create();

            //음성파일을 바이트코드로 변환
            ByteString audioBytes = ByteString.copyFrom(Files.readAllBytes(path));

            //음성 인식 요청의 구성을 설정(오디오 인코딩, 언어 코드, 샘플 레이트 등)
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.MP3)
                    .setLanguageCode("ko-KR")
                    .setSampleRateHertz(44100) //샘플링 레이트 맞추기
                    .build();

            //음성 인식 요청에 사용할 오디오 데이터를 설정
            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(audioBytes)
                    .build();

            //Google Cloud Speech-to-Text API를 호출하고, 응답받음
            RecognizeResponse response = speechClient.recognize(config, audio);

            List<SpeechRecognitionResult> results = response.getResultsList();

            if (!response.getResultsList().isEmpty()) {
                resultText = response.getResultsList().get(0).getAlternativesList().get(0).getTranscript();
            }
            System.out.println(resultText);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void STT_REST() throws IOException {
        Path path = Paths.get("C:/Users/USER_20211214/Desktop/sound/test.mp3");

        String resultText = "";

        String apiKey = "AIzaSyD4aQkvI-_O9flNES20RTR3UYzgJDNKoAw";
        try {
            // 요청을 보낼 URL 생성
            String url = String.format("https://speech.googleapis.com/v1p1beta1/speech:recognize?key=%s", apiKey);

            // 음성 파일을 바이트 코드로 변환
            byte[] audioBytes = Files.readAllBytes(path);
            String base64Audio = Base64.getEncoder().encodeToString(audioBytes);

            // JSON 요청 본문 생성
            Map<String, Object> audioConfigMap = new HashMap<>();
            audioConfigMap.put("encoding", "MP3");
            audioConfigMap.put("languageCode", "ko-KR");
            audioConfigMap.put("sampleRateHertz", 44100);

            Map<String, Object> audioMap = new HashMap<>();
            audioMap.put("content", base64Audio);

            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("config", audioConfigMap);
            requestMap.put("audio", audioMap);

            // RestTemplate을 사용하여 HTTP POST 요청 보내기
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestMap, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            // 응답 JSON 파싱하여 결과 텍스트 얻기
            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonResponse = objectMapper.readTree(response.getBody());
                JsonNode results = jsonResponse.get("results");

                if (results.isArray() && results.size() > 0) {
                    JsonNode alternatives = results.get(0).get("alternatives");
                    if (alternatives.isArray() && alternatives.size() > 0) {
                        resultText = alternatives.get(0).path("transcript").asText();
                    }
                }
            }
            System.out.println(resultText);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void TTS() throws IOException {
        String text = "안녕";
        byte[] bytes = newsService.synthesizeText(text);

        playAudio(bytes);
    }

    @Test
    public void 크롤링_음성출력() throws IOException {
        String link = "https://n.news.naver.com/mnews/article/022/0003872395?sid=100";
        String text = newsService.crawlNewsBody(link);
        System.out.println(text);

        byte[] bytes = newsService.synthesizeText(text);

        // 음성 데이터 재생
        playAudio(bytes);
    }

    public void playAudio(byte[] audioData) {
        try {
            // Create an input stream from the byte array
            ByteArrayInputStream inputStream = new ByteArrayInputStream(audioData);

            // Create a Player instance with the input stream
            Player player = new Player(inputStream);

            // Play the audio in a separate thread
            Thread playerThread = new Thread(() -> {
                try {
                    player.play();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            playerThread.start();

            //playerThread.join()을 사용하여 메인 스레드가 재생 스레드가 완료될 때까지 기다리도록 하여 문제를 해결할 수 있습니다.
            playerThread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}