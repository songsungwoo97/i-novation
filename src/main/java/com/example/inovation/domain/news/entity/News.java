package com.example.inovation.domain.news.entity;

import com.example.inovation.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class News extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column
    private String link;

    @Column(nullable = false, length = 100)
    private String author;

    private String thumbnailUrl;

    @Builder
    public News(String title, String content, String link, String author, String thumbnailUrl) {

        this.title = title;
        this.content = content;
        this.link = link;
        this.author = author;
        this.thumbnailUrl = thumbnailUrl;
    }
}
