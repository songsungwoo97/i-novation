package com.example.inovation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import io.netty.channel.ChannelOption;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {

	DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();

	//HttpClient 총 연결 시간을 10초로 설정
	HttpClient httpClient = HttpClient.create()
		.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
	@Bean
	public WebClient webClient() {
		factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

		return WebClient.builder()
			.uriBuilderFactory(factory)
			.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
			.clientConnector(new ReactorClientHttpConnector(httpClient))
			.build();
	}
}
