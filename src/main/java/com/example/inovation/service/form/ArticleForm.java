package com.example.inovation.service.form;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ArticleForm {

    private String title;
    private String link;
    private String description;
    private String image;

    public ArticleForm(String title, String link, String description, String image) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.image = image;
    }
}
