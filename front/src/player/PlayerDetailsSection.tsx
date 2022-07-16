import React from "react"
import CountryImage from "../common/elements/CountryImage"
import {ageFormatter, salaryFormatter} from "../common/Formatters"
import ExecutableComponent from "../common/sections/HookExecutableComponent"
import {playerDetails} from "../rest/Client"
import PlayerDetails from "../rest/models/player/PlayerDetails"
import PlayerLevelDataProps from "./PlayerLevelDataProps"


const PlayerDetailsSection = (props: {playerProps: PlayerLevelDataProps}) => {
    


    const content = (setRequest: (request: number) => void, _setState: (state: PlayerDetails) => void, data?: PlayerDetails) => {
        return  <div className='d-flex flex-row'>
            <div style={{position: 'relative', width: '120px', height: '160px'}}>
                {data?.avatar.map(avatarPart => <img src={'https://hattrick.org' + avatarPart.url}  
                    style={{position: 'absolute', left: avatarPart.x + 'px', top: avatarPart.y + 'px'}}/>)}
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

        <table className='small-font border border-1 bg-light shadow-sm border-secondary overflow-visible mt-2 table'>
            <tbody>
                <tr>
                    <td>TSI</td>
                    <td></td>
                </tr>
                <tr>
                    <td>Salary</td>
                    <td></td>
                </tr>
                <tr>
                    <td>Experience</td>
                    <td></td>
                </tr>
                <tr>
                    <td>Age</td>
                    <td></td>
                </tr>
            </tbody>
        </table>

        <table className='small-font border border-1 bg-light shadow-sm border-secondary overflow-visible mt-2 table'>
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
                {data?.playerSeasonStats.map(statEntry => 
                    <tr>
                        <td>{statEntry.season + props.playerProps.seasonOffset()}</td>
                        <td>{statEntry.leagueGoals}</td>
                        <td>{statEntry.cupGoals}</td>
                        <td>{statEntry.allGoals}</td>
                        <td>{statEntry.yellowCards}</td>
                        <td>{statEntry.redCards}</td>
                        <td>{statEntry.matches}</td>
                        <td>{statEntry.playedMinutes}</td>
                    </tr>
                )}
            </tbody>
        </table>
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
