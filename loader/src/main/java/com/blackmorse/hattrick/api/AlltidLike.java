package com.blackmorse.hattrick.api;

import com.blackmorse.hattrick.ScheduledCountryLoader;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AlltidLike {
    @Data
    @AllArgsConstructor
    private static class LeagueTimeJson {
        private Integer leagueId;
        private Date time;
    }

    private final String webUrl;

    @Autowired
    public AlltidLike(@Value("${web.url}") String webUrl) {
        this.webUrl = webUrl;
    }

    public void updateRoundInfo(Integer season, Integer leagueId, Integer round) {
        try(CloseableHttpClient httpClient = HttpClients.createDefault();) {
            HttpPost post = new HttpPost(webUrl + "/loader/leagueRound?season=" + season + "&leagueId=" + leagueId + "&round=" + round);

            try(CloseableHttpResponse response = httpClient.execute(post)) {
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void scheduleInfo(List<ScheduledCountryLoader.LeagueTime> leagueTimes) {
        log.info("Sending scheduleInfo request...");
        ObjectMapper objectMapper = new ObjectMapper();

        TimeZone htZone = TimeZone.getTimeZone("CET");
        TimeZone currentZone = TimeZone.getDefault();
        Integer rawOffset = htZone.getRawOffset() - currentZone.getRawOffset();

        try(CloseableHttpClient httpClient = HttpClients.createDefault();) {
            HttpPost post = new HttpPost(webUrl + "/loader/scheduleInfo");

            List<LeagueTimeJson> timeJsons = leagueTimes.stream().map(leagueTime ->
                    new LeagueTimeJson(leagueTime.league.getLeagueId(), new Date(leagueTime.time.getTime() + rawOffset)))
                    .collect(Collectors.toList());

            StringEntity stringEntity = new StringEntity(objectMapper.writeValueAsString(timeJsons));
            post.addHeader("Content-Type", "application/json");
            post.setEntity(stringEntity);

            try(CloseableHttpResponse response = httpClient.execute(post)) {
                log.info("ScheduleInfo request success");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void loadingStarted(Integer leagueId) {
        log.info("Sending leagueStarted request for league {}...", leagueId);

        try(CloseableHttpClient httpClient = HttpClients.createDefault();) {
            HttpPost post = new HttpPost(webUrl + "/loader/loadingStarted?leagueId=" + leagueId);
            try(CloseableHttpResponse response = httpClient.execute(post)) {
                log.info("leagueStarter request for {} - OK", leagueId);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
