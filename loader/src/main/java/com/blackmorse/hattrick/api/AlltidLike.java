package com.blackmorse.hattrick.api;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class AlltidLike {

    private final String webUrl;

    @Autowired
    public AlltidLike(@Value("${web.url}") String webUrl) {
        this.webUrl = webUrl;
    }

    public void updateRoundInfo(Integer season, Integer leagueId, Integer round) {
        try(CloseableHttpClient httpClient = HttpClients.createDefault();) {
            HttpPost post = new HttpPost(webUrl + "?season=" + season + "&leagueId=" + leagueId + "&round=" + round);

            try(CloseableHttpResponse response = httpClient.execute(post)) {
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
