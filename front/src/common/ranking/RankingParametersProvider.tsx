import RankingParameters from "./RankingParameters";
import i18n from "../../i18n";
import { commasSeparated, ageFormatter, ratingFormatter, injuryFormatter, salaryFormatter, loddarStats, dateNumberFormatter } from '../Formatters'
import { PagesEnum } from "../enums/PagesEnum";

class RankingParametersProvider {
    static HATSTATS(): RankingParameters {
        return {
            title: i18n.t('table.hatstats'),
            positionFunc: teamRanking => teamRanking.hatstatsPosition,
            valueFunc: teamRanking => teamRanking.hatstats,
            formatter: commasSeparated,
            sortingField: 'hatstats',
            page: PagesEnum.TEAM_HATSTATS
        }
    }

    static SALARY(rate?: number, currency?: string): RankingParameters {
        return {
            title: i18n.t('table.salary') + ', ' + currency, 
            positionFunc: teamRanking => teamRanking.salaryPosition,
            valueFunc: teamRanking => teamRanking.salary,
            formatter: value => salaryFormatter(value, rate),
            sortingField: 'sum_salary',
            page: PagesEnum.TEAM_SALARY_TSI,
            yAxisFunc: n => n / ((rate === undefined) ? 1 : rate)
        }
    }

    static TSI(): RankingParameters {
        return {
            title: i18n.t('table.tsi'),
            positionFunc: teamRanking => teamRanking.tsiPosition,
            valueFunc: teamRanking => teamRanking.tsi,
            formatter: commasSeparated,
            sortingField: 'team_tsi',
            page: PagesEnum.TEAM_SALARY_TSI
        }
    }

    static ATTACK(): RankingParameters {
        return {
            title: i18n.t('table.attack'),
            positionFunc: teamRanking => teamRanking.attackPosition,
            valueFunc: teamRanking => teamRanking.attack,
            formatter: commasSeparated,
            sortingField: 'attack',
            page: PagesEnum.TEAM_HATSTATS
        }
    }

    static DEFENSE(): RankingParameters {
        return {
            title: i18n.t('table.defense'),
            positionFunc: teamRanking => teamRanking.defensePosition,
            valueFunc: teamRanking => teamRanking.defense,
            formatter: commasSeparated,
            sortingField: 'defense',
            page: PagesEnum.TEAM_HATSTATS
        }
    }

    static MIDFIELD(): RankingParameters {
        return {
            title: i18n.t('table.midfield'),
            positionFunc: teamRanking => teamRanking.midfieldPosition,
            valueFunc: teamRanking => teamRanking.midfield,
            formatter: commasSeparated,
            sortingField: 'midfield',
            page: PagesEnum.TEAM_HATSTATS
        }
    }

    static AGE(): RankingParameters {
        return {
            title: i18n.t('table.age'),
            positionFunc: teamRanking => teamRanking.agePosition,
            valueFunc: teamRanking => teamRanking.age,
            formatter: ageFormatter,
            sortingField: 'age',
            page: PagesEnum.TEAM_AGE_INJURY
        }
    }

    static RATING(): RankingParameters {
        return {
            title: i18n.t('table.rating'),
            positionFunc: teamRanking => teamRanking.ratingPosition,
            valueFunc: teamRanking => teamRanking.rating,
            formatter: ratingFormatter,
            sortingField: 'rating',
            page: PagesEnum.TEAM_RATINGS,
            yAxisFunc: y => y / 10
        }
    }

    static RATING_END_OF_MATCH(): RankingParameters {
        return {
            title: i18n.t('table.rating_end_of_match'),
            positionFunc: teamRanking => teamRanking.ratingEndOfMatchPosition,
            valueFunc: teamRanking => teamRanking.ratingEndOfMatch,
            formatter: ratingFormatter,
            sortingField: 'rating_end_of_match',
            page: PagesEnum.TEAM_RATINGS,
            yAxisFunc: y => y / 10
        }
    }

    static POWER_RATINGS(): RankingParameters {
        return {
            title: i18n.t('table.power_rating'),
            positionFunc: teamRanking => teamRanking.powerRatingPosition,
            valueFunc: teamRanking => teamRanking.powerRating,
            formatter: commasSeparated,
            sortingField: 'power_rating',
            page: PagesEnum.TEAM_POWER_RATINGS
        }
    }

    static INJURY(): RankingParameters {
        return {
            title: i18n.t('table.total_injury_weeks'),
            positionFunc: teamRanking => teamRanking.injuryPosition,
            valueFunc: teamRanking => teamRanking.injury,
            formatter: injuryFormatter,
            sortingField: 'injury',
            page: PagesEnum.TEAM_AGE_INJURY
        }
    }

    static INJURY_COUNT(): RankingParameters {
        return {
            title: i18n.t('table.total_injury_number'),
            positionFunc: teamRanking => teamRanking.injuryCountPosition,
            valueFunc: teamRanking => teamRanking.injuryCount,
            formatter: commasSeparated,
            sortingField: 'injury_count',
            page: PagesEnum.TEAM_AGE_INJURY
        }
    }

    static LODDAR_STATS(): RankingParameters {
        return {
            title: i18n.t('table.loddar_stats'),
            positionFunc: teamRanking => teamRanking.loddarStatsPosition,
            valueFunc: teamRanking => teamRanking.loddarStats,
            formatter: loddarStats,
            sortingField: 'loddar_stats',
            page: PagesEnum.TEAM_HATSTATS
        }
    }

    static FOUNDED_DATE(): RankingParameters {
        return {
            title: i18n.t('team.date_of_foundation'),
            positionFunc: teamRanking => teamRanking.foundedPosition,
            valueFunc: teamRanking => new Date(teamRanking.founded).getTime(),
            formatter: dateNumberFormatter,
            sortingField: 'founded_date',
            page: PagesEnum.OLDEST_TEAMS
        }
    }
}

export default RankingParametersProvider
