import React from "react";
import i18n from "../../i18n";
import {ageFormatter, commasSeparated, loddarStats, ratingFormatter, redCards, salaryFormatter, yellowCards} from "../Formatters";
import LeagueUnitLink from "../links/LeagueUnitLink";
import TeamLink from "../links/TeamLink";
import TableColumn from "./TableColumn";

type SortingKeyType = {teamId: number, teamName: string, leagueId: number, leagueUnitId: number, leagueUnitName: string}
type LeagueUnit = {leagueUnitId: number, leagueUnitName: string}

class TableColumns {
    static postitionsTableColumn<Smth>(): TableColumn<Smth> {
        return {
            columnHeader: {
                title: i18n.t('table.position_abbr'), poppedHint: i18n.t('table.position')
            },
            columnValue: {
                provider: (_pst, index) => (index + 1).toString()
            }
        }
    }


    static teamTableColumn<T>(sortingFieldFunc: (t: T) => SortingKeyType, showCountryFlags?: boolean): TableColumn<T> {
        return {
            columnHeader: {title: i18n.t('table.team')},
            columnValue: {
                provider: (t) =><TeamLink
                                   id={sortingFieldFunc(t).teamId}
                                   text={sortingFieldFunc(t).teamName}
                                   flagCountryNumber={showCountryFlags !== undefined && showCountryFlags ? sortingFieldFunc(t).leagueId : undefined}
                                />, 
            }
        }
    }


    static leagueUnitTableColumn<T>(sortingFieldFunc: (t: T) => LeagueUnit): TableColumn<T> {
        return {
            columnHeader: {title: i18n.t('table.league'), center: true},
            columnValue: {
                provider: (pst) => <LeagueUnitLink 
                              id={sortingFieldFunc(pst).leagueUnitId}
                              text={sortingFieldFunc(pst).leagueUnitName}
                          />,
                center: true
            }
        }
    }

    static hatstatsTableColumn<T>(hatstatsFunc: (t: T) => number, sortingField: string): TableColumn<T> {
        return {
            columnHeader: {title: i18n.t('table.hatstats'), sortingField: sortingField, center: true},
            columnValue: {provider: t => hatstatsFunc(t).toString(), center: true}
        }
    }

    static loddarStatsTableColumn<T>(loddarStatsFunc: (t: T) => number, sortingField: string): TableColumn<T> {
        return {
            columnHeader: {title: i18n.t('table.loddar_stats'), sortingField: sortingField, center: true},
            columnValue: {provider: t => loddarStats(loddarStatsFunc(t)), center: true}
        }
    }

     static ageTableColumn<T>(ageFunc: (t: T) => number, sortingField: string): TableColumn<T> {
        return {
            columnHeader: { title: i18n.t('table.age'), sortingField: sortingField, center: true },
            columnValue:  { provider: t => ageFormatter(ageFunc(t)), center: true }
        }
     }

     static yellowCards<T>(yellowCardsFunc: (t: T) => number, sortingField: string): TableColumn<T> {
        return {
            columnHeader: { title: i18n.t('table.yellow_cards'), sortingField: sortingField, center: true },
            columnValue:  { provider: t => yellowCards(yellowCardsFunc(t)), center: true }
        }
     }

     static redCards<T>(redCardsFunc: (t: T) => number, sortingField: string): TableColumn<T> {
        return {
            columnHeader: { title: i18n.t('table.red_cards'), sortingField: sortingField, center: true },
            columnValue:  { provider: t => redCards(redCardsFunc(t)), center: true }
        }
     }

     static ratings<T>(ratingsFunc: (t: T) => number, title: string, sortingField: string): TableColumn<T> {
        return {
            columnHeader: { title: title, sortingField: sortingField, center: true },
            columnValue:  { provider: t => ratingFormatter(ratingsFunc(t)), center: true }
        }
     }

     static tsi<T>(tsiFunc: (t: T) => number, title: string, sortingField: string): TableColumn<T> {
        return {
            columnHeader: { title: title, sortingField: sortingField, center: true },
            columnValue:  { provider: t => commasSeparated(tsiFunc(t)), center: true }
        }
     }

     static salary<T>(salaryFunc: (t: T) => number, rate: number, title: string, sortingField: string): TableColumn<T> {
        return {
            columnHeader: { title: title, sortingField: sortingField, center: true },
            columnValue:  { provider: t => salaryFormatter(salaryFunc(t), rate), center: true }
        }
     }
}

export default TableColumns
