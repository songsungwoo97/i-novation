package com.example.inovation.domain.news.dto;

import lombok.*;

/**
 * 뉴스 크롤링 결과 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsContentDto {
    private String title;
    private String content;
    private String author;
}
