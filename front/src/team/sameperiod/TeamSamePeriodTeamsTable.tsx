import React, {useState} from 'react'
import {useTranslation} from 'react-i18next'
import SortingTableTh from '../../common/elements/SortingTableTh'
import {PagesEnum} from '../../common/enums/PagesEnum'
import {dateFormatter} from '../../common/Formatters'
import {LevelDataPropsWrapper} from '../../common/LevelDataProps'
import LeagueUnitLink from '../../common/links/LeagueUnitLink'
import TeamLink from '../../common/links/TeamLink'
import TeamData from '../../rest/models/leveldata/TeamData'
import {SortingDirection} from '../../rest/models/StatisticsParameters'
import CreatedSameTimeTeamExtended, {CreatedSameTimeTeam} from '../../rest/models/team/CreatedSameTimeTeamExtended'
import TeamLevelDataProps from '../TeamLevelDataProps'
import '../../common/tables/TableSection.css'

interface Props {
    data?: Array<CreatedSameTimeTeamExtended>
    levelDataPropsWrapper: LevelDataPropsWrapper<TeamData, TeamLevelDataProps>
    rowsWithTitle: Array<RowWithTitle>
}

export interface RowWithTitle {
    title: string,
    valueFunc: (createdSameTimeTeam: CreatedSameTimeTeam) => number
    rowFunc: (createdSameTimeTeam: CreatedSameTimeTeam) => JSX.Element | string | number
}

const TeamSamePeriodTeamsTable = (props: Props) => {
    const t = useTranslation().t
    const [[sortingField, sortingFunc, sortingDirection], setSorting] = useState([props.rowsWithTitle[0].title, props.rowsWithTitle[0].valueFunc, SortingDirection.DESC])


    const sortData = () => {
        if (props.data === undefined) {
            return []
        }
        const multiplier = (sortingDirection === SortingDirection.ASC) ? 1 : -1
        return  [...props.data].sort((a, b) => multiplier * (sortingFunc(a.createdSameTimeTeam) - sortingFunc(b.createdSameTimeTeam)))
    }

    return <table className="table table-striped table-rounded table-sm small">
            <thead>
                <tr>
                    <th className="text-center">{t('table.team')}</th>
                    <th className="text-center">{t('table.league')}</th>
                    {props.rowsWithTitle.map(rowWithTitle =>{
                        return <SortingTableTh 
                            title={rowWithTitle.title}
                            sortingField={rowWithTitle.title}
                            sortingState={{
                                callback: _sortBy => {
                                    if (sortingField === rowWithTitle.title) {
                                        let newSortingDirection = (sortingDirection === SortingDirection.ASC) ? SortingDirection.DESC : SortingDirection.ASC
                                        setSorting([sortingField, sortingFunc, newSortingDirection])
                                    } else {
                                        setSorting([rowWithTitle.title, rowWithTitle.valueFunc, SortingDirection.DESC])
                                    }
                                },
                                currentSorting: sortingField,
                                sortingDirection: sortingDirection
                            }}
                        />
                    } ) }
                    <th className="text-center">{t('filter.season')}</th>
                    <th className="text-center">{t('team.date_of_foundation')}</th>     
                    <th></th>               
                </tr>
            </thead>
            <tbody>
                {sortData().map(team => {
                        return <tr className={(team.createdSameTimeTeam.teamSortingKey.teamId === props.levelDataPropsWrapper.levelDataProps.teamId()) ? 'selected_row' : ''} key={'TeamSamePeriodTeams_' + team.createdSameTimeTeam.teamSortingKey.teamId}>
                            <td><TeamLink text={team.createdSameTimeTeam.teamSortingKey.teamName} id={team.createdSameTimeTeam.teamSortingKey.teamId}/></td>
                            <td className="text-center"><LeagueUnitLink id={team.createdSameTimeTeam.teamSortingKey.leagueUnitId} text={team.createdSameTimeTeam.teamSortingKey.leagueUnitName} /></td>
                            {props.rowsWithTitle.map(rowWithTitle => {
                                return <td className='text-center'>{rowWithTitle.rowFunc(team.createdSameTimeTeam)}</td>
                            })}
                            <td className="text-center">{team.season + props.levelDataPropsWrapper.levelDataProps.levelData.seasonOffset}</td>
                            <td className="text-center">{dateFormatter(team.createdSameTimeTeam.foundedDate)}</td>
                            <td className="text-center">
                                {(team.createdSameTimeTeam.teamSortingKey.teamId === props.levelDataPropsWrapper.levelDataProps.teamId()) ? <></> :
                                <TeamLink text={t('team.compare')}
                                    id={props.levelDataPropsWrapper.levelDataProps.teamId()} 
                                    page={PagesEnum.TEAM_COMPARSION}
                                    queryParams={{
                                        teamId: team.createdSameTimeTeam.teamSortingKey.teamId
                                    }}
                                    forceRefresh={true} />
                                }
                            </td>
                        </tr>
                    })}
            </tbody>
        </table>
}

export default TeamSamePeriodTeamsTable
