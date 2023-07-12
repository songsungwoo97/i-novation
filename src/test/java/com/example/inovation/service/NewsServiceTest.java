package com.example.inovation.service;

<<<<<<< HEAD
import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
=======
>>>>>>> origin/master
import javazoom.jl.player.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
<<<<<<< HEAD
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

=======

import java.io.ByteArrayInputStream;
import java.io.IOException;
>>>>>>> origin/master
@ExtendWith(SpringExtension.class)
@SpringBootTest
class NewsServiceTest {

    @Autowired
    private NewsService newsService;

    @Test
<<<<<<< HEAD
    public void STT() throws IOException {
        Path path = Paths.get("C:/Users/USER_20211214/Desktop/test.flac"); // 음성 파일의 경로를 지정합니다.

        String resultText = "";

        try {
            //SpeechClient인스턴스 생성
            SpeechClient speechClient = SpeechClient.create();

            //음성파일을 바이트코드로 변환
            ByteString audioBytes = ByteString.copyFrom(Files.readAllBytes(path));

            //음성 인식 요청의 구성을 설정(오디오 인코딩, 언어 코드, 샘플 레이트 등)
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.FLAC)
                    .setLanguageCode("ko-KR")
                    .setSampleRateHertz(48000) //샘플링 레이트 맞추기
                    .setAudioChannelCount(2) //채널 수 맞추기
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
            System.out.println(resultText);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
=======
>>>>>>> origin/master
    public void TTS() throws IOException {
        String text = "안녕";
        byte[] bytes = newsService.synthesizeText(text);

        playAudio(bytes);
    }

    @Test
    public void 크롤링() throws IOException {
        String link = "https://n.news.naver.com/mnews/article/011/0004190069?sid=101";
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
<<<<<<< HEAD


=======
>>>>>>> origin/master
}