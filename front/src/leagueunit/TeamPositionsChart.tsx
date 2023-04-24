import React, { useState } from 'react'
import PlotlyChart from 'react-plotly.js';
import { useTranslation } from 'react-i18next'
import ExecutableComponent, { StateAndRequest } from '../common/sections/HookExecutableComponent'
import { getTeamPositionsHistory } from '../rest/Client'
import { LeagueUnitTeamStat } from '../rest/models/team/LeagueUnitTeamStat'
import LeagueUnitLevelDataProps from './LeagueUnitLevelDataProps';
import { Col, Container, Row } from 'react-bootstrap';
import SeasonSelector from '../common/selectors/SeasonSelector';

interface Request {
    round: number
    season: number
}


const TeamPositionsChart = (leagueUnitPropsWrapper: {leagueUnitProps: LeagueUnitLevelDataProps}) => {
    let leagueUnitProps = leagueUnitPropsWrapper.leagueUnitProps
    const [ t, _i18n ] = useTranslation()
    const [ season, setSeason ] = useState(leagueUnitProps.currentSeason())

    const content = (stateAndRequest: StateAndRequest<Request, Array<LeagueUnitTeamStat> | undefined>) => {
        let positionsHistory = stateAndRequest.currentState
        if (positionsHistory === undefined) {
            return <></>
        }
        let currentRound = positionsHistory.reduce((a, b) => a.round > b.round ? a : b).round
        let x = Array.from({length: currentRound}, (_, i) => i + 1)

        let map: Map<string, Array<number>> = new Map()
         
        positionsHistory
            .sort((a ,b) => (a.round < b.round) ? -1 : 1)
            .forEach (teamStat => {
                if (map.get(teamStat.teamName) === undefined) {
                    map.set(teamStat.teamName, new Array<number>())
                }
                map.get(teamStat.teamName)?.push(teamStat.position)
            })
        let chartData = Array.from(map.keys()).map(key => {
            return {
                type: 'scatter',
                name: key,
                x: x,
                y: map.get(key)
            }
        })

        let layout = {
            autosize: false,
            width: '100%',
            height: 300,
            margin: {
                t: 20,
                r: 0
            },
            legend: {
                orientation: 'h'
            },
            yaxis: {
                autorange: "reversed",
                nticks: 8,
                dtick: 1,
                title: {
                    text: t('table.position')
                }
            }
        }
        

        return <Container className='table-responsive'>
            <Row>
                <Col lg={3} md={6}>
                    <SeasonSelector currentSeason={season}
                        seasonOffset={leagueUnitProps.seasonOffset()}
                        seasons={leagueUnitProps.seasons()}
                        callback={setSeason}
                    />
                </Col>
            </Row>
            <Row>
                <PlotlyChart data={chartData} layout={layout} />
            </Row>
            </Container>
        
    }
    return <ExecutableComponent<Request, Array<LeagueUnitTeamStat> | undefined>
        executeRequest={(_request, callback) => getTeamPositionsHistory(leagueUnitProps.leagueUnitId(), season, callback)}
        initialRequest={{round: leagueUnitProps.currentRound(), season: leagueUnitProps.currentSeason()}}
        content={content}
        responseToState={s => s}
    />
}
 export default TeamPositionsChart
