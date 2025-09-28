import i18n from "../../i18n";
import Mappings from "../enums/Mappings";
import {ageFormatter, commasSeparated, injuryFormatter, loddarStats, ratingFormatter, redCards, salaryFormatter, yellowCards} from "../Formatters";
import LeagueUnitLink from "../links/LeagueUnitLink";
import PlayerLink from "../links/PlayerLink";
import TeamLink from "../links/TeamLink";
import TableColumn from "./TableColumn";

type SortingKeyType = {teamId: number, teamName: string, leagueId: number, leagueUnitId: number, leagueUnitName: string}
type PlayerSortingKeyType = {playerId: number, firstName: string, lastName: string, nationality: number}
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

     static ageTableColumn<T>(ageFunc: (t: T) => number, sortingField?: string): TableColumn<T> {
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

     static player<T>(sortingKeyFunc: (t: T) => PlayerSortingKeyType, countriesMap: Map<number, string>): TableColumn<T> {
            return {
                columnHeader: {title: i18n.t('table.player')},
                columnValue: {
                    provider: (pst) => <PlayerLink 
                                           id={sortingKeyFunc(pst).playerId}
                                           text={sortingKeyFunc(pst).firstName + ' ' + sortingKeyFunc(pst).lastName}
                                           nationality={sortingKeyFunc(pst).nationality}
                                           countriesMap={countriesMap}
                                           externalLink
                                        />
                }
            }
     }

     static role<T>(roleFunc: (t: T) => string): TableColumn<T> {
         return {
                columnHeader: {title: ''},
                columnValue: { provider: (pst) => i18n.t(Mappings.roleToTranslationMap.get(roleFunc(pst)) || ''), center: true }
            }
     }

     static simpleNumber<T>(numberFunc: (t: T) => number, title: {title: string, poppedHint?: string}, sortingField?: string): TableColumn<T> {
         return {
             columnHeader: {title: title.title, center: true, poppedHint: title.poppedHint, sortingField: sortingField},
             columnValue: {provider: (t) => numberFunc(t).toString(), center: true}
         }
     }

     static injury<T>(injuryFunc: (t: T) => number, sortingField?: string): TableColumn<T> {
         return {
             columnHeader: { title: i18n.t('table.injury'), sortingField: sortingField, center: true },
             columnValue: { provider: (t) => injuryFormatter(injuryFunc(t))}
         }
     }
}

export default TableColumns
