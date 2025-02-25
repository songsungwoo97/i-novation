package com.example.inovation.domain.news.dto;

import com.example.inovation.domain.news.entity.News;
import lombok.*;

/**
 * 인기 뉴스 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsForm {
    private Long id;
    private String title;
    private String description;
    private String link;
    private String thumbnailUrl;

    public static NewsForm from(News news) {
        return NewsForm.builder()
                .id(news.getId())
                .title(news.getTitle())
                .description(news.getContent())
                .link(news.getLink())  // DB에 저장된 링크가 없다면 빈 값
                .thumbnailUrl(news.getThumbnailUrl())
                .build();
    }
}