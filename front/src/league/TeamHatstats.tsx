import React from 'react';
import {getTeamRatings} from '../rest/Client';
import TeamRating from '../rest/models/TeamRating';
import ModelTable from '../common/ModelTable';
import '../i18n'
import { Translation } from 'react-i18next'



class TeamHatstats extends ModelTable<TeamRating> {
    sectionTitle(): string  {
        return 'menu.best_teams'
    } 

    fetchEntities = getTeamRatings
        
    columnHeaders(): JSX.Element {
        return <Translation>
            {
            (t, { i18n }) =>
            <tr>
                <th className="position hint" popped-hint={t('table.position')}>{t('table.position_abbr')}</th>
                <th>{t('table.team')}</th>
                <th className="value">{t('table.league')}</th>
                <th className="value">{t('table.hatstats')}</th>
                <th className="value">{t('table.midfield')}</th>
                <th className="value">{t('table.defense')}</th>
                <th className="value">{t('table.attack')}</th>
            </tr>
        }
        </Translation>
    }

    columnValues(index: number, teamRating: TeamRating): JSX.Element {
        return <tr key={"team_hatstats_row_" + index}>
            <td>{index + 1}</td>
            <td><a className="table_link" href="/#">{teamRating.teamName}</a></td>
            <td className="value"><a className="table_link" href="/#">{teamRating.leagueUnitName}</a></td>
            <td className="value">{teamRating.hatStats}</td>
            <td className="value">{teamRating.midfield * 3}</td>
            <td className="value">{teamRating.defense}</td>
            <td className="value">{teamRating.attack}</td>
        </tr>
    }

}

export default TeamHatstats