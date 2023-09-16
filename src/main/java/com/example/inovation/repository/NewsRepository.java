package com.example.inovation.repository;

import com.example.inovation.domain.News;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {


    List<News> findAllByTitleContaining(String keyword);
}
