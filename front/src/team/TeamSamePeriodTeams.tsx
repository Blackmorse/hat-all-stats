import React from 'react';
import { LevelDataPropsWrapper } from '../common/LevelDataProps'
import TeamData from '../rest/models/leveldata/TeamData'
import TeamLevelDataProps from './TeamLevelDataProps'
import CreatedSameTimeTeamExtended from '../rest/models/team/CreatedSameTimeTeamExtended';
import { LoadingEnum } from '../common/enums/LoadingEnum';
import { getCreatedSameTimeTeams } from '../rest/Client'
import TeamLink from '../common/links/TeamLink'
import LeagueUnitLink from '../common/links/LeagueUnitLink';
import '../common/tables/TableSection.css'
import { Translation } from 'react-i18next'
import { PagesEnum } from '../common/enums/PagesEnum';
import ExecutableComponent from '../common/sections/ExecutableComponent';
import Section, { SectionState } from '../common/sections/Section';
import { dateFormatter } from '../common/Formatters';

interface State {
    teams?: Array<CreatedSameTimeTeamExtended>
}

class TeamSamePeriodTeamsBase extends ExecutableComponent<LevelDataPropsWrapper<TeamData, TeamLevelDataProps>, 
    State & SectionState, Array<CreatedSameTimeTeamExtended>, string> {
    
        constructor(props: LevelDataPropsWrapper<TeamData, TeamLevelDataProps>) {
        super(props)
        this.state = {
            loadingState: LoadingEnum.OK,
            dataRequest: 'season',
            collapsed: false
        }
    }

    executeDataRequest(dataRequest: string, callback: (loadingState: LoadingEnum, result?: CreatedSameTimeTeamExtended[]) => void): void {
        getCreatedSameTimeTeams(this.props.levelDataProps.leagueId(), 
            this.props.levelDataProps.levelData.foundedDate, dataRequest, callback)
    }

    stateFromResult(result?: Array<CreatedSameTimeTeamExtended>): State & SectionState {
        return {
            teams: result,
            collapsed: this.state.collapsed
        }
    }

    onChanged = (event: React.FormEvent<HTMLSelectElement>) => {
        let period = event.currentTarget.value

        this.updateWithRequest(period)
    }
    
    renderSection(): JSX.Element {
        if (this.state.teams === undefined) {
            return <></>
        }

        return  <Translation>{
            (t, { i18n }) => <div className='table-responsive'>
            <div>
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
            <table className="table table-striped table-rounded table-sm small">
                <thead>
                    <tr>
                        <th className="text-center">{t('table.team')}</th>
                        <th className="text-center">{t('table.league')}</th>
                        <th className="text-center">{t('table.power_rating')}</th>
                        <th className="text-center">{t('filter.season')}</th>
                        <th className="text-center">{t('chart.round')}</th>
                        <th className="text-center">{t('team.date_of_foundation')}</th>     
                        <th></th>               
                    </tr>
                </thead>
                <tbody>
                    {this.state.teams?.filter(team => team.createdSameTimeTeam.teamSortingKey.teamId !== this.props.levelDataProps.teamId())
                        .map(team => {
                            return <tr key={this.constructor.name + '_' + team.createdSameTimeTeam.teamSortingKey.teamId}>
                                <td><TeamLink text={team.createdSameTimeTeam.teamSortingKey.teamName} id={team.createdSameTimeTeam.teamSortingKey.teamId}/></td>
                                <td className="text-center"><LeagueUnitLink id={team.createdSameTimeTeam.teamSortingKey.leagueUnitId} text={team.createdSameTimeTeam.teamSortingKey.leagueUnitName} /></td>
                                <td className="text-center">{team.createdSameTimeTeam.powerRating}</td>
                                <td className="text-center">{team.season + this.props.levelDataProps.levelData.seasonOffset}</td>
                                <td className="text-center">{team.round}</td>
                                <td className="text-center">{dateFormatter(team.createdSameTimeTeam.foundedDate)}</td>
                                <td className="text-center"><TeamLink text='Compare' 
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
            </div>
        }
        </Translation>
    }
}

const TeamSamePeriodTeams = Section(TeamSamePeriodTeamsBase, _ => 'menu.created_same_time_teams')
export default TeamSamePeriodTeams