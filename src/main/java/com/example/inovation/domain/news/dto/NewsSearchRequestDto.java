package com.example.inovation.domain.news.dto;

import lombok.*;

/**
 * 네이버 뉴스 검색 키워드 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsSearchRequestDto {
    private String keyword;

    private Integer display;
}