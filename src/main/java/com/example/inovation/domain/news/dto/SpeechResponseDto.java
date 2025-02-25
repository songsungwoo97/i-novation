package com.example.inovation.domain.news.dto;

import lombok.*;

/**
 * 음성 변환 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpeechResponseDto {
    private byte[] audioData;
    private String contentType;  // 예: "audio/mpeg"
    private String fileName;     // 예: "news_audio.mp3"
}