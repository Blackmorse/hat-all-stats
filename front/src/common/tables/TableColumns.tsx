import React from "react";
import i18n from "../../i18n";
import {loddarStats} from "../Formatters";
import LeagueUnitLink from "../links/LeagueUnitLink";
import TeamLink from "../links/TeamLink";
import TableColumn from "./TableColumn";

type SortingKeyType = {teamId: number, teamName: string, leagueId: number, leagueUnitId: number, leagueUnitName: string}

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


    static leagueUnitTableColumn<T>(sortingFieldFunc: (t: T) => SortingKeyType): TableColumn<T> {
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
}

export default TableColumns
