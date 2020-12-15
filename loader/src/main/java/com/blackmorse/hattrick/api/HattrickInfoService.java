package com.blackmorse.hattrick.api;

import com.blackmorse.hattrick.HattrickService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

@Component
public class HattrickInfoService {
    private final HattrickService hattrickService;
    private Map<Integer, Integer> countryIdToLeagueIdMap;

    @Autowired
    public HattrickInfoService(HattrickService hattrickService) {
        this.hattrickService = hattrickService;
    }

    @PostConstruct
    public void init() {
        countryIdToLeagueIdMap = hattrickService.countryIdToLeagueIdMap();
    }

    public Map<Integer, Integer> getCountryIdToLeagueIdMap() {
        return countryIdToLeagueIdMap;
    }

}
