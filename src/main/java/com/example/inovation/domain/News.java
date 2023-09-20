package com.example.inovation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@ToString
@NoArgsConstructor
@Getter @Setter
public class News {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "news_id")
    private Long id;

    private String title;
    private String url;
    private String image;
    //private String category; //Query로 처리해주기

    @Builder
    public News(String title, String url, String image) {

        this.title = title;
        this.url = url;
        this.image = image;
    }
}
