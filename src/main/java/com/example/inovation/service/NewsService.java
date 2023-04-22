package com.example.inovation.service;

import com.example.inovation.domain.News;
import com.example.inovation.repository.NewsRepository;
import com.example.inovation.service.form.NewsForm;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class NewsService {

    private final NewsRepository newsRepository;

    @Transactional
    public List<NewsForm> NaverNewsCrawler() throws IOException {

        String URL = "https://news.naver.com/main/ranking/popularDay.naver";
        Document doc = Jsoup.connect(URL).get();

        List<NewsForm> newsList = new ArrayList<>();

        Elements elements = doc.select("div.rankingnews_box > ul > li");
        //Elements contents = doc.getElementsByAttributeValue("class", "rankingnews_box_wrap _popularRanking");


        for (Element element : elements) {
            String title = element.select("a.list_title").text();
            String url = element.select("a.list_title").attr("href");
            String image = element.select("a.list_img > img").attr("src");

            NewsForm news = new NewsForm(title, url, image);


            //newsRepository.save(news);
            newsList.add(news);
        }
        return newsList;
    }

}
