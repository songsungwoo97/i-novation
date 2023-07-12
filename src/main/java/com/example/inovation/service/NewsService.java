package com.example.inovation.service;

import com.example.inovation.service.Error.NewsApiException;
import com.example.inovation.service.form.ArticleForm;
import com.fasterxml.jackson.databind.JsonNode;
<<<<<<< HEAD
import com.google.cloud.speech.v1.*;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
=======
>>>>>>> origin/master
import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
<<<<<<< HEAD
import org.springframework.web.multipart.MultipartFile;
=======
>>>>>>> origin/master

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class NewsService {

    @Value("${naver.api.clientId}")
    private String clientId;

    @Value("${naver.api.clientSecret}")
    private String clientSecret;

    //private final NewsRepository newsRepository;

    private final RestTemplate restTemplate; // api 사용을 위해

    @Configuration
    public static class RestTemplateConfig {
        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }
    }

    @Transactional
    public List<ArticleForm> searchNaverNews(String query, int size) throws IOException {
        // news_office_code를 추가하여 네이버 뉴스 기사만 검색합니다.
        String url = "https://openapi.naver.com/v1/search/news.json?query=" + query + "&display=" + size;
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class, query, size);

        if (response.getStatusCode() == HttpStatus.OK) {
            List<ArticleForm> articles = new ArrayList<>();
            JsonNode items = Objects.requireNonNull(response.getBody()).get("items");

            if (items.isArray()) {
                for (JsonNode item : items) {
                    String title = item.get("title").asText();
                    String link = item.get("link").asText();
                    String description = item.get("description").asText();

                    title = Jsoup.clean(title, Safelist.none()); //HTML 태그 정리
                    description = Jsoup.clean(description, Safelist.none());

                    if (isNaverNewsLink(link)) {
                        String thumbnail = getThumbnailFromLink(link);
                        ArticleForm articleForm = new ArticleForm(title, link, description, thumbnail);
                        articles.add(articleForm);
                    }
                }
            }

            return articles;
        } else {
            throw new NewsApiException((HttpStatus) response.getStatusCode());
        }
    }

    //네이버 뉴스인지 찾아주는 함수
    private boolean isNaverNewsLink(String link) {
        return link.contains("news.naver.com");
    }

    //썸네일을 크롤링해주는 함수
    private String getThumbnailFromLink(String link) throws IOException {
        Document doc = Jsoup.connect(link).get();
        Element imageElement = doc.selectFirst("meta[property=og:image]");
        return imageElement != null ? imageElement.attr("content") : "";
    }

<<<<<<< HEAD
    /*#################################################################################################*/

//TTS 구현
=======
/*#################################################################################################*/
>>>>>>> origin/master

    public String crawlNewsBody(String url) {
        try {
            Document doc = Jsoup.connect(url).get();

            Elements newsBodyElements = doc.select("div.go_trans._article_content");

            // 사진설명 제거
            newsBodyElements.select(".end_photo_org").remove();

            // 뉴스의 본문을 추출
            return newsBodyElements.text();

        } catch (Exception e) {
            e.printStackTrace();
            return "본문을 추출할 수 없음.";
        }
    }

    public byte[] synthesizeText(String text) throws IOException {
        int maxTextLength = 100; //1000개로 하면 오류 발생
        List<String> textChunk = splitTextIntoChunks(text, maxTextLength); //문자열을 1000개 단위로 나누고 리스트에 저장
        List<byte[]> audioDataList = new ArrayList<>();

        //TTS 사용(클라이언트 생성)
        try {
            for (String chunk : textChunk) {
                TextToSpeechClient client = TextToSpeechClient.create();
                VoiceSelectionParams voice = VoiceSelectionParams.newBuilder() //음성파라미터를 설정
                        .setLanguageCode("ko-KR")
                        .build();

                AudioConfig audioConfig = AudioConfig.newBuilder() //오디오의 인코딩 형식을 지정
<<<<<<< HEAD
                        .setAudioEncoding(com.google.cloud.texttospeech.v1.AudioEncoding.MP3)
=======
                        .setAudioEncoding(AudioEncoding.MP3)
>>>>>>> origin/master
                        .build();

                SynthesisInput input = SynthesisInput.newBuilder() //TTS엔진에 전달할 텍스트입력 설정
                        .setText(chunk)
                        .build();

                SynthesizeSpeechResponse response = client.synthesizeSpeech(input, voice, audioConfig); //TTS클라이언트에 음성 합성 요청을 보냄
                ByteString audioData = response.getAudioContent(); //오디오 데이터를 가져옴
                audioDataList.add(audioData.toByteArray());

                client.shutdown(); //기존 호출은 계속되지만 새로운 호출은 허용되지 않는 정돈된 종료를 시작합니다.
                try {
                    if (!client.awaitTermination(800, TimeUnit.MILLISECONDS)) { //이 메소드는 채널이 종료될 때까지 블록하며, 시간 제한이 도달하면 포기합니다.
                        System.out.println("Timeout");
                        client.shutdownNow(); //기존 호출과 새로운 호출이 취소되는 강제 종료를 시작합니다.
                    }
                } catch (InterruptedException e) {
                    System.out.println("Shutdown error");
                    client.shutdownNow();
                }
            }

        } catch (Exception e) {
            System.out.println("inner");
            System.out.println(e);
        }

        return mergeAudioData(audioDataList); //리스트를 하나의 배열로 병합
    }


    private byte[] mergeAudioData(List<byte[]> audioDataList) throws IOException {//음성 데이터를 List에서 하나의 배열로 병합
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        for (byte[] audioData : audioDataList) {
            outputStream.write(audioData);
        }
        outputStream.close();
        return outputStream.toByteArray();
    }


    private List<String> splitTextIntoChunks(String text, int chunkLength) {// 텍스트를 1000개 단위로 나눔
        List<String> chunks = new ArrayList<>();
        for (int i = 0; i < text.length(); i += chunkLength) {
            int endIndex = Math.min(i + chunkLength, text.length());
            String chunk = text.substring(i, endIndex);
            chunks.add(chunk);
        }

        return chunks;
    }
<<<<<<< HEAD
    /*#################################################################################################*/

    //음성인식 구현
    public String speechKeyword(MultipartFile file) {

        String resultText = "";

        try {
            //SpeechClient인스턴스 생성
            SpeechClient speechClient = SpeechClient.create();

            //음성파일을 바이트코드로 변환
            ByteString audioBytes = ByteString.copyFrom(file.getBytes());

            //음성 인식 요청의 구성을 설정(오디오 인코딩, 언어 코드, 샘플 레이트 등)
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(AudioEncoding.LINEAR16)
                    .setLanguageCode("ko-KR")
                    .setSampleRateHertz(16000)
                    .build();

            //음성 인식 요청에 사용할 오디오 데이터를 설정
            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(audioBytes)
                    .build();

            //Google Cloud Speech-to-Text API를 호출하고, 응답받음
            RecognizeResponse response = speechClient.recognize(config, audio);

            List<SpeechRecognitionResult> results = response.getResultsList();

            for (SpeechRecognitionResult result : results) {
                //가장 근접한 값을 저장
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                resultText += alternative.getTranscript();
            }
            return resultText;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
=======

}
>>>>>>> origin/master
