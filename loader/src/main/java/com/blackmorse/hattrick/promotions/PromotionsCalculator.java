package com.blackmorse.hattrick.promotions;

import com.blackmorse.hattrick.api.worlddetails.model.League;
import com.blackmorse.hattrick.promotions.model.*;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class PromotionsCalculator {
    private static final Map<Integer, DivisionDownStrategy> downStrategies = new HashMap<>();

    static {
        downStrategies.put(1, new DivisionDownStrategy(DownStrategy.REVERSE, DownStrategy.FORWARD));
        downStrategies.put(2, new DivisionDownStrategy(DownStrategy.REVERSE, DownStrategy.FORWARD));
        downStrategies.put(3, new DivisionDownStrategy(DownStrategy.REVERSE, DownStrategy.FORWARD));
        downStrategies.put(4, new DivisionDownStrategy(DownStrategy.REVERSE, DownStrategy.FORWARD));
        downStrategies.put(5, new DivisionDownStrategy(DownStrategy.REVERSE, DownStrategy.FORWARD));
        downStrategies.put(6, new DivisionDownStrategy(DownStrategy.REVERSE, DownStrategy.REVERSE));
        downStrategies.put(7, new DivisionDownStrategy(DownStrategy.REVERSE, DownStrategy.REVERSE));
        downStrategies.put(8, new DivisionDownStrategy(DownStrategy.REVERSE, DownStrategy.REVERSE));
        downStrategies.put(9, new DivisionDownStrategy(DownStrategy.REVERSE, DownStrategy.REVERSE));
        downStrategies.put(10, new DivisionDownStrategy(DownStrategy.NONE, DownStrategy.NONE));
    }

    private final Map<Integer, DivisionTeams> divisionTeams = new HashMap<>();
    private final PromotionListsMerger<PromoteTeam, Promotion> merger;// = new PromotionListsMerger<>(Promotion::new);

    public PromotionsCalculator(League league, Integer season) {
        for (int divisionLevel = 1; divisionLevel <= league.getNumberOfLevels() - 1; divisionLevel++) {
            divisionTeams.put(divisionLevel, new DivisionTeams(divisionLevel, downStrategies.get(divisionLevel)));
        }

        divisionTeams.put(league.getNumberOfLevels(), new DivisionTeams(league.getNumberOfLevels(),
                new DivisionDownStrategy(DownStrategy.NONE, DownStrategy.NONE)));

        this.merger = new PromotionListsMerger<>((l1, l2) -> new Promotion(league.getLeagueId(), season, l1, l2));
    }

    public void addPromoteTeam(PromoteTeam promoteTeam) {
        divisionTeams.get(promoteTeam.getTeam().getLeagueUnit().getLevel()).teams.add(promoteTeam);
    }

    private static final Comparator<PromoteTeam> straightComparator =
            Comparator.comparing(PromoteTeam::getPosition)
                .thenComparing(promoteTeam -> -promoteTeam.getPoints())
                .thenComparing(promoteTeam -> -promoteTeam.getDiff())
                .thenComparing(promoteTeam -> -promoteTeam.getScored());

    private static final Comparator<PromoteTeam> reverseComparator = straightComparator.reversed();

    public List<Promotion> calculatePromotionsForDivision(Integer divisionLevel) {
        DivisionDownStrategy downStrategy = divisionTeams.get(divisionLevel).downStrategy;

        List<PromoteTeam> qualifyGoingDownTeams = divisionTeams.get(divisionLevel).teams.stream()
                .filter(promoteTeam -> promoteTeam.getPosition() == 5 || promoteTeam.getPosition() == 6)
                .sorted(straightComparator)
                .peek(promoteTeam -> promoteTeam.promoteType = PromoteType.QUALIFY)
                .collect(Collectors.toList());

        List<PromoteTeam> autoGoingDownTeams = divisionTeams.get(divisionLevel).teams.stream()
                .filter(promoteTeam -> promoteTeam.getPosition() == 7 || promoteTeam.getPosition() == 8)
                .sorted(straightComparator)
                .peek(promoteTeam -> promoteTeam.promoteType = PromoteType.AUTO)
                .collect(Collectors.toList());

        List<PromoteTeam> goingUpTeams = divisionTeams.get(divisionLevel + 1).teams.stream()
                .sorted(straightComparator)
                .limit(qualifyGoingDownTeams.size() + autoGoingDownTeams.size())
                .collect(Collectors.toList());


        List<PromoteTeam> autoGoingUpTeams = goingUpTeams.stream()
                .limit(autoGoingDownTeams.size())
                .sorted(downStrategy.getAutoPromoteStrategy().equals(DownStrategy.FORWARD) ? straightComparator : reverseComparator)
                .peek(promoteTeam -> promoteTeam.promoteType = PromoteType.AUTO)
                .collect(Collectors.toList());

        List<PromoteTeam> qualifyGoingUpTeams = goingUpTeams.stream()
                .skip(autoGoingDownTeams.size())
                .limit(qualifyGoingDownTeams.size())
                .sorted(downStrategy.getQualifyPromoteStrategy().equals(DownStrategy.FORWARD) ? straightComparator : reverseComparator)
                .peek(promoteTeam -> promoteTeam.promoteType = PromoteType.QUALIFY)
                .collect(Collectors.toList());

        List<Promotion> result = new ArrayList<>();

        List<Promotion> autoPromotions = merger.mergeTeams(autoGoingDownTeams, autoGoingUpTeams);
        List<Promotion> qualifyPromotions = merger.mergeTeams(qualifyGoingDownTeams, qualifyGoingUpTeams);

        result.addAll(autoPromotions);
        result.addAll(qualifyPromotions);

        return result;
    }


}
