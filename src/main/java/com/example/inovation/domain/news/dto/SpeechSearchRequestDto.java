package com.example.inovation.domain.news.dto;

import lombok.*;

/**
 * 음성으로 검색 시 사용하는 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpeechSearchRequestDto {
    private byte[] audioData;    // 음성 데이터 (바이너리)
    private String audioFormat;  // 예: "wav", "mp3"
}
