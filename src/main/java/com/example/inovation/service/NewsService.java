package com.example.inovation.service;

import com.example.inovation.service.Error.NewsApiException;
import com.example.inovation.service.form.ArticleForm;
import com.example.inovation.service.form.NewsForm;
import com.fasterxml.jackson.databind.JsonNode;

import com.google.cloud.speech.v1.*;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;

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

import org.springframework.web.multipart.MultipartFile;


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

    private final RestTemplate restTemplate; // api 사용을 위해 deprecated
    //private final WebClient webClient;


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

    public List<NewsForm> getPopularNews() throws IOException {

        List<NewsForm> form = new ArrayList<>();

        Document doc = Jsoup.connect("https://news.google.com/topics/CAAqJggKIiBDQkFTRWdvSUwyMHZNRFZxYUdjU0FtdHZHZ0pMVWlnQVAB?hl=ko&gl=KR&ceid=KR%3Ako").get();

        Elements elements = doc.select("div.afJ4ge c-wiz");

        for(Element element : elements) {
            System.out.println("element" + element);
        }

        for(Element element : elements) {
            String title = element.select("h4.gPFEn").text();
            String link = element.select("a.WwrzSb").attr("abs:href");
            String thumbnail = element.select("img").attr("src");

            System.out.println("title: " + title);
            System.out.println("link: " + link);
            System.out.println("thumbnail: " + thumbnail);

            NewsForm articleForm = new NewsForm(title, link, thumbnail); // description은 예시로 비워 둠.
            form.add(articleForm);
        }

        return form;
    }


//TTS 구현
    public String crawlNewsBody(String url) {
        try {
            Document doc = Jsoup.connect(url).get();

            Elements newsBodyElements = doc.select("article.go_trans._article_content");


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
                        .setAudioEncoding(com.google.cloud.texttospeech.v1.AudioEncoding.MP3)
                        .setAudioEncoding(com.google.cloud.texttospeech.v1.AudioEncoding.MP3)
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
/*
public byte[] synthesizeText(String text) throws IOException {
    int maxTextLength = 100;
    List<String> textChunk = splitTextIntoChunks(text, maxTextLength);
    List<byte[]> audioDataList = new ArrayList<>();

    String apiKey = "AIzaSyD4aQkvI-_O9flNES20RTR3UYzgJDNKoAw"; // 실제 API 키로 교체해주세요.

    for (String chunk : textChunk) {
        try {
            String url = String.format("https://texttospeech.googleapis.com/v1/text:synthesize?key=%s", apiKey);
            RestTemplate restTemplate = new RestTemplate();

            // HTTP 요청 본문을 구성합니다.
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("input", Map.of("text", chunk));
            requestMap.put("voice", Map.of("languageCode", "ko-KR"));
            requestMap.put("audioConfig", Map.of("audioEncoding", "MP3"));

            // API에 HTTP POST 요청을 보냅니다.
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestMap, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                String audioContent = (String) response.getBody().get("audioContent");
                byte[] audioData = Base64.getDecoder().decode(audioContent);
                audioDataList.add(audioData);
            } else {
                System.out.println("Error: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.out.println("inner");
            System.out.println(e);
        }
    }

    return mergeAudioData(audioDataList);
}
*/


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

    /*
    public String speechKeyword(MultipartFile file) {
        String resultText = "";
        String apiKey = "AIzaSyD4aQkvI-_O9flNES20RTR3UYzgJDNKoAw";
        try {
            String url = String.format("https://speech.googleapis.com/v1/speech:recognize?key=%s", apiKey);

            // 음성 파일을 바이트 코드로 변환
            ByteString audioBytes = ByteString.copyFrom(file.getBytes());

            // JSON 요청 본문 생성
            Map<String, Object> audioConfigMap = new HashMap<>();
            audioConfigMap.put("encoding", "LINEAR16");
            audioConfigMap.put("languageCode", "ko-KR");
            audioConfigMap.put("sampleRateHertz", 16000);

            Map<String, Object> audioMap = new HashMap<>();
            audioMap.put("content", audioBytes.toStringUtf8());

            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("config", audioConfigMap);
            requestMap.put("audio", audioMap);

            // RestTemplate을 사용하여 HTTP POST 요청 보내기
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestMap, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            // 응답 JSON 파싱하여 결과 텍스트 얻기
            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonResponse = objectMapper.readTree(response.getBody());
                JsonNode results = jsonResponse.get("results");

                if (results.isArray()) {
                    for (JsonNode result : results) {
                        JsonNode alternatives = result.get("alternatives");
                        if (alternatives.isArray()) {
                            JsonNode alternative = alternatives.get(0);
                            resultText += alternative.get("transcript").asText() + " ";
                        }
                    }
                }
            }

            return resultText.trim();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
*/
}
