import React from "react"
import {Col, Row} from "react-bootstrap"
import {useTranslation} from "react-i18next"
import CountryImage from "../common/elements/CountryImage"
import Mappings from "../common/enums/Mappings"
import {ageFormatter, injuryFormatter, salaryFormatter, stringSalaryFormatter} from "../common/Formatters"
import LeagueLink from "../common/links/LeagueLink"
import LeagueUnitLink from "../common/links/LeagueUnitLink"
import TeamLink from "../common/links/TeamLink"
import ExecutableComponent, {StateAndRequest} from "../common/sections/HookExecutableComponent"
import {playerDetails} from "../rest/Client"
import PlayerDetails from "../rest/models/player/PlayerDetails"
import PlayerHistoryChart from "./PlayerHistoryChart"
import PlayerLevelDataProps from "./PlayerLevelDataProps"


const PlayerDetailsSection = (props: {playerProps: PlayerLevelDataProps}) => {
    const [t, i18n] = useTranslation() 

    const content = (stateAndRequest: StateAndRequest<number, PlayerDetails | undefined>) => {
        if (stateAndRequest.currentState === undefined) return <></>
        return  <div className='d-flex flex-column'>
            <Row className='align-items-center row'>
                <Col lg={5} md={12}>
                    <div className='d-flex flex-row'>
                        <div className='mx-4' style={{position: 'relative', width: '120px', height: '160px'}}>
                            {stateAndRequest.currentState.avatar.map(avatarPart => <img src={'https://hattrick.org' + avatarPart.url}  
                                style={{position: 'absolute', left: avatarPart.x + 'px', top: avatarPart.y + 'px'}}/>)}
                        </div>
                        <Col>
                            <table style={{maxWidth: '220px'}} className='small-font border border-1 bg-light shadow-sm border-secondary overflow-visible m-2 table'>
                                <tr>
                                    <td>{t('table.age')}</td>
                                    <td className='text-center'>{ageFormatter(stateAndRequest.currentState.currentPlayerCharacteristics.age)}</td>
                                </tr>
                                <tr>
                                    <td>{t('player.experience')}</td>
                                    <td className='text-center'>{props.playerProps.skillLevelTranslation(i18n.resolvedLanguage, stateAndRequest.currentState.currentPlayerCharacteristics.experience)}</td>
                                </tr>
                                <tr>
                                    <td>{t('table.tsi')}</td>
                                    <td className='text-center'>{salaryFormatter(stateAndRequest.currentState.currentPlayerCharacteristics.tsi)}</td>
                                </tr>
                                <tr>
                                    <td>{t('table.salary')}</td>
                                    <td className='text-center'>{salaryFormatter(stateAndRequest.currentState.currentPlayerCharacteristics.salary, props.playerProps.currencyRate())} {props.playerProps.currency()}</td>
                                </tr>
                            </table>
                            <table style={{maxWidth: '220px'}} className='col small-font border border-1 bg-light shadow-sm border-secondary overflow-visible m-2 table'>
                                <tr>
                                    <td>{t('player.form')}</td>
                                    <td className='text-center'>{props.playerProps.skillLevelTranslation(i18n.resolvedLanguage, stateAndRequest.currentState.currentPlayerCharacteristics.form)}</td>
                                </tr>
                                <tr>
                                    <td>{t('player.speciality')}</td>
                                    <td className='text-center'>{props.playerProps.specialityTranslation(i18n.resolvedLanguage, stateAndRequest.currentState.currentPlayerCharacteristics.speciality + 1)}</td>
                                </tr>
                                <tr>
                                    <td>{t('table.position')}</td>
                                    <td className='text-center'>{i18n.t(Mappings.roleToTranslationMap.get(stateAndRequest.currentState.currentPlayerCharacteristics.position) || '')}</td>
                                </tr>
                                <tr>
                                    <td>{t('table.injury')}</td>
                                    <td className='text-center'>{injuryFormatter(stateAndRequest.currentState.currentPlayerCharacteristics.injuryLevel)}</td>
                                </tr>
                            </table>
                        </Col>
                    </div>
                </Col>
                <Col lg={4}>
                    <table  className='col small-font border border-1 bg-light shadow-sm border-secondary overflow-visible m-2 table'>
                        <thead>
                            <th>{t('filter.season')}</th>
                            <th>{t('table.league')}</th>
                            <th>{t('player.cup')}</th>
                            <th>{t('dream_team.total')}</th>
                            <th>{t('player.yellow')}</th>
                            <th>{t('player.red')}</th>
                            <th>{t('menu.matches')}</th>
                            <th>{t('player.minutes')}</th>
                        </thead>
                        <tbody>
                            {stateAndRequest.currentState?.playerSeasonStats.entries.map(entry => 
                                <tr>
                                    <td className='text-center'>{entry.season}</td>
                                    <td className='text-center'>{entry.leagueGoals}</td>
                                    <td className='text-center'>{entry.cupGoals}</td>
                                    <td className='text-center'>{entry.allGoals}</td>
                                    <td className='text-center'>{entry.yellowCards}</td>
                                    <td className='text-center'>{entry.redCards}</td>
                                    <td className='text-center'>{entry.matches}</td>
                                    <td className='text-center'>{salaryFormatter(entry.playedMinutes)}</td>
                                </tr>
                            )}

                        </tbody>
                        <tfoot>
                            <tr className='table-active'>
                                <td>Total</td>
                                <td className='text-center'>{stateAndRequest.currentState?.playerSeasonStats.totalLeagueGoals}</td>
                                <td className='text-center'>{stateAndRequest.currentState?.playerSeasonStats.totalCupGoals}</td>
                                <td className='text-center'>{stateAndRequest.currentState?.playerSeasonStats.totalAllGoals}</td>
                                <td className='text-center'>{stateAndRequest.currentState?.playerSeasonStats.totalYellowCards}</td>
                                <td className='text-center'>{stateAndRequest.currentState?.playerSeasonStats.totalRedCard}</td>
                                <td className='text-center'>{stateAndRequest.currentState?.playerSeasonStats.totalMatches}</td>
                                <td className='text-center'>{salaryFormatter(stateAndRequest.currentState?.playerSeasonStats.totalPlayedMinutes)}</td>
                            </tr>
                        </tfoot>
                    </table>
                </Col>

            </Row>
        <div>


        <table className='small-font border border-1 bg-light shadow-sm border-secondary overflow-visible mt-2 table'>
            <thead>
                <th></th>
                <th></th>
                <th className='text-center'>{t('table.team')}</th>
                <th></th>
                <th></th>
                <th></th>
                <th className='text-center'>{t('table.team')}</th>
                <th></th>
                <th className='text-center'>{t('table.age')}</th>
                <th className='text-center'>{t('table.tsi')}</th>
                <th className='text-center'>{t('table.salary')}</th>
            </thead>
            <tbody>
                {stateAndRequest.currentState?.playerLeagueUnitHistory.map(entry =>
                <tr>
                    <td>{entry.season + props.playerProps.seasonOffset()} ({entry.round})</td>
                    <td>
                        <LeagueLink id={entry.fromLeagueId} text={
                            <CountryImage text={props.playerProps.countriesMap().get(entry.fromLeagueId)} countryId={entry.fromLeagueId}/>
                            } 
                        />
                    </td>
                    <td className='text-center align-middle'>
                        <TeamLink 
                            id={entry.fromTeamId}
                            text={entry.fromTeamName}
                        />
                    </td>
                    <td className='text-center'>
                        <LeagueUnitLink
                            id={entry.fromLeagueUnitId}
                            text={entry.fromLeagueUnitName}
                        />
                    </td>
                    <td className='text-center'>{'->'}</td>
                    <td className='text-center'>
                        <LeagueUnitLink
                            id={entry.toLeagueUnitId}
                            text={entry.toLeagueUnitName}
                        />
                    </td>
                    <td className='text-center align-middle'>
                        <TeamLink
                            id={entry.toTeamId}
                            text={entry.toTeamName}
                        />
                    </td>
                    <td>
                        <LeagueLink id={entry.toLeagueId} text={
                            <CountryImage text={props.playerProps.countriesMap().get(entry.toLeagueId)} countryId={entry.toLeagueId}/>
                            }
                        />
                    </td>
                    <td className='text-center'>{ageFormatter(entry.age)}</td>
                    <td className='text-center'>{salaryFormatter(entry.tsi)}</td>
                    <td className='text-center'>{salaryFormatter(entry.salary, props.playerProps.currencyRate())} {props.playerProps.currency()}</td>
                </tr>)}
            </tbody>
        </table>


        </div>
        <Row>
            <Col lg={5} style={{width: '500px'}}>
                <PlayerHistoryChart history={stateAndRequest.currentState?.playerCharts} 
                    title='TSI'
                    chartLines={[{
                        valueFunc: pce => pce.tsi,
                        formatter: stringSalaryFormatter,
                        title: 'TSI',
                        color :'green'
                    }]}
                />            
            </Col>
            <Col lg={5} style={{width: '500px'}}>
                <PlayerHistoryChart history={stateAndRequest.currentState?.playerCharts}
                    title={t('table.salary') + ', ' + props.playerProps.currency()}
                    chartLines={[{
                        valueFunc: pce => pce.salary,
                        formatter: value => stringSalaryFormatter(value, props.playerProps.currencyRate()),
                        title: t('table.salary') + ' ,' + props.playerProps.currency(),
                        color: 'green'
                    }]}
                />
            </Col>

            <Col lg={5} style={{width: '500px'}}>
                <PlayerHistoryChart history={stateAndRequest.currentState?.playerCharts.filter(x => x.rating !== 0)}
                    title={t('table.rating')}
                    chartLines={[
                    {
                        valueFunc: pce => pce.rating,
                        formatter: n => (n / 10).toString(),
                        title: t('table.rating'),
                        color: 'green'
                    }
                    ]}
                />
            </Col>
            <Col lg={5}  style={{width: '500px'}}>
                <PlayerHistoryChart history={stateAndRequest.currentState?.playerCharts.filter(x => x.ratingEndOfMatch !== 0)}
                    title={t('table.rating_end_of_match')}
                    chartLines={[
                    {
                        valueFunc: pce => pce.ratingEndOfMatch,
                        formatter: n => (n / 10).toString(),
                        title: t('table.rating_end_of_match'),
                        color: 'green'
                    }
                    ]}
                />
            </Col>
        </Row>
        </div>
    }

    return <ExecutableComponent<number, PlayerDetails | undefined>
        executeRequest={playerDetails} 
        responseToState={response => response}
        initialRequest={props.playerProps.playerId()}
        content={content}
        sectionTitle={playerDetails => (playerDetails === undefined) ? '' : playerDetails.firstName + ' ' + playerDetails.lastName}
    />
}

export default PlayerDetailsSection
