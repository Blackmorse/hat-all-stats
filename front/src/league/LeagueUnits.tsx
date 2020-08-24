import React from 'react';
import LeagueUnitRating from '../rest/models/LeagueUnitRating'
import ModelTable from '../common/ModelTable'
import { getLeagueUnits } from '../rest/Client'
import { Translation } from 'react-i18next'
import '../i18n'

class LeagueUnits extends ModelTable<LeagueUnitRating> {

    sectionTitle(): string  {
        return 'menu.best_league_units'
    }

    fetchEntities = getLeagueUnits

    columnHeaders(): JSX.Element {
        return <Translation>{
            (t, { i18n }) => <tr>
                <th className="position hint" popped-hint={t('table.position')}>{t('table.position_abbr')}</th>
                <th className="value">{t('table.league')}</th>
                <th className="value">{t('table.hatstats')}</th>
                <th className="value">{t('table.midfield')}</th>
                <th className="value">{t('table.defense')}</th>
                <th className="value">{t('table.attack')}</th>
            </tr>
            }
            </Translation> 
    }

    columnValues(index: number, leagueUnitRating: LeagueUnitRating): JSX.Element {
        return <tr key={"league_unit_row_" + index}>
            <td>{index + 1}</td>
            <td className="value"><a className="table_link" href="/#">{leagueUnitRating.leagueUnitName}</a></td>
            <td className="value">{leagueUnitRating.hatStats}</td>
            <td className="value">{leagueUnitRating.midfield * 3}</td>
            <td className="value">{leagueUnitRating.defense}</td>
            <td className="value">{leagueUnitRating.attack}</td>
        </tr>
    }    
}

export default LeagueUnits;