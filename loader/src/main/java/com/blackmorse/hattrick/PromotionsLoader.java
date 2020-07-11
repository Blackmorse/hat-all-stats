package com.blackmorse.hattrick;

import com.blackmorse.hattrick.api.Hattrick;
import com.blackmorse.hattrick.api.worlddetails.model.League;
import com.blackmorse.hattrick.clickhouse.ClickhouseWriter;
import com.blackmorse.hattrick.model.LeagueUnit;
import com.blackmorse.hattrick.promotions.*;
import com.blackmorse.hattrick.promotions.model.PromoteTeam;
import com.blackmorse.hattrick.promotions.model.Promotion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class PromotionsLoader {
    private final HattrickService hattrickService;
    private final ClickhouseWriter<Promotion> promotionClickhouseWriter;
    private final Hattrick hattrick;

    @Autowired
    public PromotionsLoader(HattrickService hattrickService,
                            @Qualifier("promotionsWriter") ClickhouseWriter<Promotion> promotionClickhouseWriter,
                            Hattrick hattrick) {
        this.hattrickService = hattrickService;
        this.promotionClickhouseWriter = promotionClickhouseWriter;
        this.hattrick = hattrick;
    }

    public void load(String countryName) {
        List<LeagueUnit> allLeagueUnitIdsForCountry =
                hattrickService.getAllLeagueUnitIdsForCountry(countryName);

        League league = hattrickService.getLeagueByCountryName(countryName);
        log.info("Loading promotions for {}", countryName);
        log.info("There are {} league units at {}", allLeagueUnitIdsForCountry.size(), countryName);
        PromotionsCalculator promotionsCalculator = new PromotionsCalculator(league, hattrick.getSeason());

        List<PromoteTeam> promoteTeams = hattrickService.getPromoteTeams(allLeagueUnitIdsForCountry);

        promoteTeams.forEach(promotionsCalculator::addPromoteTeam);

        for (int i = 1; i < league.getNumberOfLevels(); i++) {
            log.info("Collecting promotions for {} division", i);
            List<Promotion> promotions = promotionsCalculator.calculatePromotionsForDivision(i);
            log.info("Writing promotions for {} division to clickhouse", i);
            promotionClickhouseWriter.writeToClickhouse(promotions);
        }

        System.out.println();
    }
}
