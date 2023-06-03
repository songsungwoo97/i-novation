package com.example.inovation.service;

import javazoom.jl.player.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
@ExtendWith(SpringExtension.class)
@SpringBootTest
class NewsServiceTest {

    @Autowired
    private NewsService newsService;

    @Test
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
}