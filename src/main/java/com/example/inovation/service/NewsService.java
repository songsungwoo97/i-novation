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
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class NewsService {

    private final NewsRepository newsRepository;


    @Transactional
    public List naverNewsCrawler() throws IOException {

        String URL = "https://news.naver.com/main/ranking/popularDay.naver";
        Document doc = Jsoup.connect(URL).get();

        //List<NewsForm> newsList = new ArrayList<>();
        List<String> resultList = new ArrayList<>();


        Elements elements = doc.select("div.rankingnews_box > ul > li");
        //Elements contents = doc.getElementsByAttributeValue("class", "rankingnews_box_wrap _popularRanking");

        int i = 0;
        for (Element element : elements) {
            String title = element.select("a.list_title").text();
            String url = element.select("a.list_title").attr("href");
            String image = element.select("a.list_img > img").attr("src");

            resultList.add(title);
            resultList.add(url);
            resultList.add(image);

            saveNews(new News(title, url, image));
            /*NewsForm newsForm = NewsForm.builder()
                    .title(title)
                    .url(url)
                    .image(image)
                    .build();

            saveNews(newsForm.toEntity());*/
            //newsList.add(newsForm);
            i++;
            if(i == 9) break;
        }
        return resultList;
    }
    @Transactional
    public void saveNews(News news) {newsRepository.save(news);}

    public List<String> search(String keyword) { //키워드로 검색

        List<News> newsSearch = newsRepository.findAllByTitleContaining(keyword);

        if(newsSearch == null) return null;

        List<String> list = new ArrayList<>();

        for(News news : newsSearch) {
            list.add(news.getTitle());
            list.add(news.getUrl());
            list.add(news.getImage());
        }
        return list;
    }
}