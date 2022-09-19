import React from "react";
import i18n from "../../i18n";
import LeagueUnitLink from "../links/LeagueUnitLink";
import TeamLink from "../links/TeamLink";
import TableColumn from "./TableColumn";

type WithSortingKey = {sortingKey: {teamId: number, teamName: string, leagueId: number, leagueUnitId: number, leagueUnitName: string}}

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


    static teamTableColumn<T extends WithSortingKey>(showCountryFlags?: boolean): TableColumn<T> {
        return {
            columnHeader: {title: i18n.t('table.team')},
            columnValue: {
                provider: (t) =><TeamLink
                                   id={t.sortingKey.teamId}
                                   text={t.sortingKey.teamName}
                                   flagCountryNumber={showCountryFlags !== undefined && showCountryFlags ? t.sortingKey.leagueId : undefined}
                                /> 
            }
        }
    }


    static leagueUnitTableColumn<T extends WithSortingKey>(): TableColumn<T> {
        return {
            columnHeader: {title: i18n.t('table.league'), center: true},
            columnValue: {
                provider: (pst) => <LeagueUnitLink 
                              id={pst.sortingKey.leagueUnitId}
                              text={pst.sortingKey.leagueUnitName}
                          />,
                center: true
            }
        }
    }
}

export default TableColumns
