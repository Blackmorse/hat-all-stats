import React from "react"
import CountryImage from "../common/elements/CountryImage"
import {ageFormatter, injuryFormatter, salaryFormatter, stringSalaryFormatter} from "../common/Formatters"
import ExecutableComponent from "../common/sections/HookExecutableComponent"
import {playerDetails} from "../rest/Client"
import PlayerDetails from "../rest/models/player/PlayerDetails"
import PlayerHistoryChart from "./PlayerHistoryChart"
import PlayerLevelDataProps from "./PlayerLevelDataProps"


const PlayerDetailsSection = (props: {playerProps: PlayerLevelDataProps}) => {
    


    const content = (_setRequest: (request: number) => void, _setState: (state: PlayerDetails) => void, data?: PlayerDetails) => {
        return  <div className='d-flex flex-column'>
            <div className='d-flex flex-row align-items-center row'>
                <div className='col-1'>
                    <div style={{position: 'relative', width: '120px', height: '160px'}}>
                        {data?.avatar.map(avatarPart => <img src={'https://hattrick.org' + avatarPart.url}  
                            style={{position: 'absolute', left: avatarPart.x + 'px', top: avatarPart.y + 'px'}}/>)}
                    </div>
                </div>
                <div className='col-2'>
                    <table className='small-font border border-1 bg-light shadow-sm border-secondary overflow-visible m-2 table'>
                        <tr>
                            <td>Age</td>
                            <td>{ageFormatter(data?.currentPlayerCharacteristics.age)}</td>
                        </tr>
                        <tr>
                            <td>Experience</td>
                            <td>{data?.currentPlayerCharacteristics.experience}</td>
                        </tr>
                        <tr>
                            <td>TSI</td>
                            <td>{salaryFormatter(data?.currentPlayerCharacteristics.tsi)}</td>
                        </tr>
                        <tr>
                            <td>Salary</td>
                            <td>{salaryFormatter(data?.currentPlayerCharacteristics.salary, props.playerProps.currencyRate())} {props.playerProps.currency()}</td>
                        </tr>
                    </table>
                </div>
                <div className='col-2'>
                    <table className='col small-font border border-1 bg-light shadow-sm border-secondary overflow-visible m-2 table'>
                        <tr>
                            <td>Form</td>
                            <td>{data?.currentPlayerCharacteristics.form}</td>
                        </tr>
                        <tr>
                            <td>Speciality</td>
                            <td>{data?.currentPlayerCharacteristics.speciality}</td>
                        </tr>
                        <tr>
                            <td>Position</td>
                            <td>{data?.currentPlayerCharacteristics.position}</td>
                        </tr>
                        <tr>
                            <td>Injury</td>
                            <td>{injuryFormatter(data?.currentPlayerCharacteristics.injuryLevel)}</td>
                        </tr>
                    </table>
                </div>
                <div className='col-4'>
                    <table className='col small-font border border-1 bg-light shadow-sm border-secondary overflow-visible m-2 table'>
                        <thead>
                            <th>Season</th>
                            <th>League</th>
                            <th>Cup</th>
                            <th>Total</th>
                            <th>Yellow</th>
                            <th>Red</th>
                            <th>Matches</th>
                            <th>Minutes</th>
                        </thead>
                        <tbody>
                            {data?.playerSeasonStats.map(entry => 
                                <tr>
                                    <td>{entry.season}</td>
                                    <td>{entry.leagueGoals}</td>
                                    <td>{entry.cupGoals}</td>
                                    <td>{entry.allGoals}</td>
                                    <td>{entry.yellowCards}</td>
                                    <td>{entry.redCards}</td>
                                    <td>{entry.matches}</td>
                                    <td>{entry.playedMinutes}</td>
                                </tr>
                            )}

                        </tbody>
                    </table>
                </div>

            </div>
        <div>


        <table className='small-font border border-1 bg-light shadow-sm border-secondary overflow-visible mt-2 table'>
            <thead>
                <th></th>
                <th></th>
                <th className='text-center'>Team</th>
                <th></th>
                <th></th>
                <th></th>
                <th className='text-center'>Team</th>
                <th></th>
                <th className='text-center'>Age</th>
                <th className='text-center'>TSI</th>
                <th className='text-center'>Salary</th>
            </thead>
            <tbody>
                {data?.playerLeagueUnitHistory.map(entry =>
                <tr>
                    <td>{entry.season + props.playerProps.seasonOffset()} ({entry.round})</td>
                    <td><CountryImage text={props.playerProps.countriesMap().get(entry.fromLeagueId)} countryId={entry.fromLeagueId}/></td>
                    <td className='text-center align-middle'>{entry.fromTeamName}</td>
                    <td className='text-center'>{entry.fromLeagueUnitName}</td>
                    <td className='text-center'>{'->'}</td>
                    <td className='text-center'>{entry.toLeagueUnitName}</td>
                    <td className='text-center align-middle'>{entry.toTeamName}</td>
                    <td><CountryImage text={props.playerProps.countriesMap().get(entry.toLeagueId)} countryId={entry.toLeagueId}/></td>
                    <td>{ageFormatter(entry.age)}</td>
                    <td>{salaryFormatter(entry.tsi)}</td>
                    <td>{salaryFormatter(entry.salary, props.playerProps.currencyRate())} {props.playerProps.currency()}</td>
                </tr>)}
            </tbody>
        </table>

        <div className="d-flex flex-row">
            <div className='col-6'>
            <PlayerHistoryChart history={data?.playerCharts} 
                title='TSI'
                chartLines={[{
                    valueFunc: pce => pce.tsi,
                    formatter: stringSalaryFormatter,
                    title: 'TSI',
                    color :'green'
                }]}
            />            
            </div>
            <div className='col-6'>
            <PlayerHistoryChart history={data?.playerCharts}
                title={'Salary' + ',' + props.playerProps.currency()}
                chartLines={[{
                    valueFunc: pce => pce.salary,
                    formatter: value => stringSalaryFormatter(value, props.playerProps.currencyRate()),
                    title: 'Salary' + ',' + props.playerProps.currency(),
                    color: 'green'
                }]}
            />
            </div>

            <div className='col-6'>
            <PlayerHistoryChart history={data?.playerCharts}
                title='Ratings'
                chartLines={[
                {
                    valueFunc: pce => pce.rating,
                    formatter: n => (n / 10).toString(),
                    title: 'Rating',
                    color: 'green'
                },
                {
                    valueFunc: pce => pce.ratingEndOfMatch,
                    formatter: n => (n / 10).toString(),
                    title: 'Rating End Of Match',
                    color: 'orange'
                }
                ]}
            />
            </div>
        </div>

            </div>
        </div>
    }

    return <ExecutableComponent<number, PlayerDetails | undefined>
        executeRequest={playerDetails} 
        responseToState={response => response}
        initialRequest={props.playerProps.playerId()}
        content={content}
        sectionTitle={playerDetails => playerDetails?.firstName + ' ' + playerDetails?.lastName}
    />
}

export default PlayerDetailsSection
