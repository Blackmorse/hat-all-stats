import React from 'react';
import ExecutableStatisticsSection from '../common/sections/ExecutableStatisticsSection'
import { LevelDataPropsWrapper } from '../common/LevelDataProps'
import TeamData from '../rest/models/leveldata/TeamData'
import TeamLevelDataProps from './TeamLevelDataProps'
import CreatedSameTimeTeamExtended from '../rest/models/team/CreatedSameTimeTeamExtended';
import { LoadingEnum } from '../common/enums/LoadingEnum';
import { getCreatedSameTimeTeams } from '../rest/Client'
import moment from 'moment'
import TeamLink from '../common/links/TeamLink'
import LeagueUnitLink from '../common/links/LeagueUnitLink';
import '../common/tables/TableSection.css'
import { Translation } from 'react-i18next'
import { PagesEnum } from '../common/enums/PagesEnum';

interface State {
    teams?: Array<CreatedSameTimeTeamExtended>
}

class TeamSamePeriodTeams extends ExecutableStatisticsSection<LevelDataPropsWrapper<TeamData, TeamLevelDataProps>, State, Array<CreatedSameTimeTeamExtended>, string> {
    constructor(props: LevelDataPropsWrapper<TeamData, TeamLevelDataProps>) {
        super(props, 'menu.created_same_time_teams')
        this.state = {
            loadingState: LoadingEnum.OK,
            dataRequest: 'season',
            state: {},
            collapsed: false
        }
    }

    executeDataRequest(dataRequest: string, callback: (loadingState: LoadingEnum, result?: CreatedSameTimeTeamExtended[]) => void): void {
        getCreatedSameTimeTeams(this.props.levelDataProps.leagueId(), 
            this.props.levelDataProps.levelData.foundedDate, dataRequest, callback)
    }

    stateFromResult(result?: Array<CreatedSameTimeTeamExtended>): State {
        return {
            teams: result,
        }
    }

    onChanged = (event: React.FormEvent<HTMLSelectElement>) => {
        let period = event.currentTarget.value

        this.updateWithRequest(period)
    }
    
    renderSection(): JSX.Element {
        if (this.state.state.teams === undefined) {
            return <></>
        }

        return  <Translation>{
            (t, { i18n }) => <><div className="table_settings_div">
                <div className="selector_div">
                    <span className="selector_div_entry">
                        {t('team.period')}:
                    </span>
                    <select className="selector_div_entry" defaultValue="season"
                            onChange={this.onChanged}>
                        <option value="round">{t('chart.round')}</option>
                        <option value="season">{t('filter.season')}</option>
                    </select>
                </div>
            </div>
            <table className="statistics_table">
                <thead>
                    <th className="value">{t('table.team')}</th>
                    <th className="value">{t('table.league')}</th>
                    <th className="value">{t('table.power_rating')}</th>
                    <th className="value">{t('filter.season')}</th>
                    <th className="value">{t('chart.round')}</th>
                    <th className="value">{t('team.date_of_foundation')}</th>     
                    <th></th>               
                </thead>
                <tbody>
                    {this.state.state.teams?.filter(team => team.createdSameTimeTeam.teamSortingKey.teamId !== this.props.levelDataProps.teamId())
                        .map(team => {
                            return <tr key={this.constructor.name + '_' + team.createdSameTimeTeam.teamSortingKey.teamId}>
                                <td><TeamLink text={team.createdSameTimeTeam.teamSortingKey.teamName} id={team.createdSameTimeTeam.teamSortingKey.teamId}/></td>
                                <td className="value"><LeagueUnitLink id={team.createdSameTimeTeam.teamSortingKey.leagueUnitId} text={team.createdSameTimeTeam.teamSortingKey.leagueUnitName} /></td>
                                <td className="value">{team.createdSameTimeTeam.powerRating}</td>
                                <td className="value">{team.season + this.props.levelDataProps.levelData.seasonOffset}</td>
                                <td className="value">{team.round}</td>
                                <td className="value">{moment(team.createdSameTimeTeam.foundedDate).format('DD.MM.YYYY')}</td>
                                <td className="value"><TeamLink text='Compare' 
                                    id={this.props.levelDataProps.teamId()} 
                                    page={PagesEnum.TEAM_COMPARSION}
                                    queryParams={{
                                        teamId: team.createdSameTimeTeam.teamSortingKey.teamId
                                    }}
                                    forceRefresh={true} /></td>
                            </tr>
                        })}
                </tbody>
            </table>
            </>
        }
        </Translation>
    }
}

export default TeamSamePeriodTeams