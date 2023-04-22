package com.example.inovation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class
Keyword {

    @Id @GeneratedValue
    @Column(name = "keyword_id")
    private Long id;
}
