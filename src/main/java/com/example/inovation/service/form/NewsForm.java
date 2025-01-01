package com.example.inovation.service.form;

import com.example.inovation.domain.News;
import lombok.*;

@NoArgsConstructor
@ToString
@Getter @Setter
public class NewsForm {
    private String title;
    private String url;
    private String image;

    @Builder
    public NewsForm(String title, String url, String image) {

        this.title = title;
        this.url = url;
        this.image = image;
    }

}
