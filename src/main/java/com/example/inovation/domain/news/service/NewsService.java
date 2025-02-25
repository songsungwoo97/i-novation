package com.example.inovation.domain.news.service;


import com.example.inovation.domain.news.dto.ArticleDto;
import com.example.inovation.domain.news.dto.NewsForm;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.cloud.speech.v1p1beta1.*;
import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import com.google.cloud.speech.v1p1beta1.RecognitionConfig.AudioEncoding;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;


@RequiredArgsConstructor
@Service
@Slf4j
@Transactional(readOnly = true)
public class NewsService {

    @Value("${naver.api.clientId}")
    private String clientId;

    @Value("${naver.api.clientSecret}")
    private String clientSecret;

    private final WebClient webClient; //RestTemplate 대신 사용하기

    public List<ArticleDto> searchNaverNews(String query, int size) throws Exception {

        String encodedQuery = UriComponentsBuilder.fromUriString(query)
                .build()
                .encode()
                .toUriString();

        String uri = "https://openapi.naver.com/v1/search/news.json?query=" + encodedQuery + "&display=" + size;

        JsonNode response = sendNaverNewsRequest(uri).getBody();
        log.info("--------------네이버 통신--------------");
        return extractArticlesFromResponse(response);
    }

    private ResponseEntity<JsonNode> sendNaverNewsRequest(String uri) {
        return webClient.get()
            .uri(uri)
            .header("X-Naver-Client-Id", clientId)
            .header("X-Naver-Client-Secret", clientSecret)
            .retrieve()
            .toEntity(JsonNode.class)
            .block();
    }

    private List<ArticleDto> extractArticlesFromResponse(JsonNode response) throws Exception {
        List<ArticleDto> articles = new ArrayList<>();
        JsonNode items = response.get("items");
        if (items.isArray()) {
            for (JsonNode item : items) {
                String title = cleanText(item.get("title").asText());
                String link = item.get("link").asText();
                String description = cleanText(item.get("description").asText());
                if (isNaverNewsLink(link)) {
                    String thumbnailUrl = getThumbnailFromLink(link);

                    ArticleDto article = ArticleDto.builder()
                            .title(title)
                            .link(link)
                            .description(description)
                            .build();

                    articles.add(article);
                }
            }
        }
        return articles;
    }

    private String cleanText(String text) {
        return Jsoup.clean(text, Safelist.none());
    }

    //네이버 뉴스인지 찾아주는 함수
    private boolean isNaverNewsLink(String link) {
        return link.contains("news.naver.com");
    }
    //썸네일을 크롤링해주는 함수
    private String getThumbnailFromLink(String link) throws Exception {
        Document doc = Jsoup.connect(link).get();
        Element imageElement = doc.selectFirst("meta[property=og:image]");
        return imageElement != null ? imageElement.attr("content") : "";
    }

    //인기뉴스 크롤링
    public List<NewsForm> getPopularNews() throws IOException {

        //구글 뉴스
        List<NewsForm> newsFormList  = new ArrayList<>();

        Document doc = Jsoup.connect("https://news.naver.com/main/ranking/popularDay.naver").get();
        Elements newsElements = doc.select(".rankingnews_box_wrap ._officeCard .rankingnews_box");

        // 각 뉴스 제목과 링크를 출력합니다.
        for (Element newsElement : newsElements) {

            Elements listElements = newsElement.select("ul.rankingnews_list");

            for (Element listElement : listElements) {
                Element linkElement = listElement.select(".list_content a.list_title").first();

                String title = linkElement.text();
                title = cleanText(title); //HTML 태그 정리

                String link = linkElement.attr("href");
                Element thumbnailElement = listElement.select("a.list_img img").first();

                String thumbnailUrl = thumbnailElement != null ? thumbnailElement.attr("src") : "no thumbnail";

                // NewsForm 객체 생성 및 추가
                NewsForm newsForm = NewsForm.builder()
                        .title(title)
                        .link(link)
                        .description("")
                        .thumbnailUrl(thumbnailUrl)// 미리보기 설명은 공백으로 설정
                        .build();

                newsFormList.add(newsForm);
            }
        }

        return newsFormList;
    }


    //TTS 구현
    public String crawlNewsBody(String url) {
        try {
            Document doc = Jsoup.connect(url).get();

            Elements newsBodyElements = doc.select("article.go_trans._article_content");

            // 사진설명 제거
            newsBodyElements.select(".end_photo_org").remove();

            // 기자 정보 제거
            //newsBodyElements.select(".byline_s").remove();

            // 뉴스 본문을 추출하고 공백을 제거
            String newsBody = newsBodyElements.text().trim();

            // 빈 문자열인 경우 null 반환
            if (newsBody.isEmpty()) {
                return null;
            }

            return newsBody;

        } catch (IOException e) {
            log.error("URL 접속 중 에러 발생", e);
            return null;
        } catch (Exception e) {
            log.error("크롤링 중 에러 발생", e);
            return null;
        }
    }

    public byte[] synthesizeText(String text) throws IOException {
        int maxTextLength = 100; //1000개로 하면 오류 발생
        List<String> textChunk = splitTextIntoChunks(text, maxTextLength); //문자열을 1000개 단위로 나누고 리스트에 저장
        //List<byte[]> audioDataList = new ArrayList<>();
        List<CompletableFuture<byte[]>> futures = new ArrayList<>();

        //TTS 사용(클라이언트 생성)
        for (String chunk : textChunk) {
            CompletableFuture<byte[]> future = CompletableFuture.supplyAsync(() -> {
                try {
                    TextToSpeechClient client = TextToSpeechClient.create();
                    VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                            .setLanguageCode("ko-KR")
                            .setSsmlGender(SsmlVoiceGender.NEUTRAL)
                            .build();

                    AudioConfig audioConfig = AudioConfig.newBuilder()
                            .setAudioEncoding(com.google.cloud.texttospeech.v1.AudioEncoding.MP3)
                            .build();

                    SynthesisInput input = SynthesisInput.newBuilder()
                            .setText(chunk)
                            .build();

                    SynthesizeSpeechResponse response = client.synthesizeSpeech(input, voice, audioConfig);
                    ByteString audioData = response.getAudioContent();
                    client.shutdown();
                    return audioData.toByteArray();
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                    return null;
                }
            });
            futures.add(future);
        }
        List<byte[]> audioDataList = futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .toList();

        return mergeAudioData(audioDataList);
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

    public String speechKeyword(byte[] word) {

        String resultText = "";

        try {
            //SpeechClient인스턴스 생성
            SpeechClient speechClient = SpeechClient.create();

            //음성 인식 요청의 구성을 설정(오디오 인코딩, 언어 코드, 샘플 레이트 등)
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(AudioEncoding.MP3)
                    .setLanguageCode("ko-KR")
                    .setSampleRateHertz(44100)
                    .build();

            //음성 인식 요청에 사용할 오디오 데이터를 설정
            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(ByteString.copyFrom(word))
                    .build();

            //Google Cloud Speech-to-Text API를 호출하고, 응답받음
            RecognizeResponse response = speechClient.recognize(config, audio);

            List<SpeechRecognitionResult> results = response.getResultsList();

            if (!response.getResultsList().isEmpty()) {
                resultText = response.getResultsList().get(0).getAlternativesList().get(0).getTranscript();
            }
            return resultText;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
