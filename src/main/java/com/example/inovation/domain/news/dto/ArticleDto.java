package com.example.inovation.domain.news.dto;

import com.example.inovation.domain.news.entity.News;
import lombok.*;

/**
 * 네이버 뉴스 검색 결과 아이템 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleDto {
    private String title;
    private String link;
    private String description;

    // News 엔티티로 변환
    public News toEntity() {
        return News.builder()
                .title(this.title.replaceAll("<[^>]*>", "")) // HTML 태그 제거
                .content(this.description.replaceAll("<[^>]*>", "")) // HTML 태그 제거
                .author("네이버 뉴스")
                .link(this.link)
                .thumbnailUrl(null) // 네이버 API는 썸네일을 제공하지 않음
                .build();
    }
}