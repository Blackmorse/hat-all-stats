import React from 'react';
import TeamRanking from '../../rest/models/team/TeamRanking'
import { Translation } from 'react-i18next'
import '../../i18n'
import './RankingTable.css'
import TeamLevelDataProps from '../TeamLevelDataProps';
import { PagesEnum } from '../../common/enums/PagesEnum';
import LeagueLink from '../../common/links/LeagueLink';
import DivisionLevelLink from '../../common/links/DivisionLevelLink';

export interface RankingData {
    lastLeagueRanking: TeamRanking,
    previousLeagueRanking?: TeamRanking,
    lastDivisionLevelRanking: TeamRanking,
    previousDivisionLevelRanking?: TeamRanking,
    teamLevelDataProps: TeamLevelDataProps,
    leagueTeamsCount: number,
    divisionLevelTeamsCount: number
}

interface Props {
    rankingData: RankingData,
    valueFunc: (teamRanking: TeamRanking) => number,
    positionFunc: (teamRanking: TeamRanking) => number,
    formatter: (value: number) => JSX.Element,
    page: PagesEnum,
    sortingField: string,
    title: string
}

class RankingTable extends React.Component<Props> {
    
    render() {
        let formatter = this.props.formatter
        let valueFunc = this.props.valueFunc
        let positionFunc = this.props.positionFunc

        let lastLeagueRanking = this.props.rankingData.lastLeagueRanking
        let previousLeagueRanking = this.props.rankingData.previousLeagueRanking
        let lastDivisionLevelRanking = this.props.rankingData.lastDivisionLevelRanking
        let previousDivisionLevelRanking = this.props.rankingData.previousDivisionLevelRanking


        let diffValueContent: JSX.Element
        let divisionLevelDiffPositionContent: JSX.Element
        if (previousDivisionLevelRanking) {
            if(valueFunc(previousDivisionLevelRanking) > valueFunc(lastDivisionLevelRanking)) {
                diffValueContent = <>
                    <img className="trend_down" src="/trend-red.png" alt="down" />
                    -{formatter((valueFunc(previousDivisionLevelRanking) - valueFunc(lastDivisionLevelRanking)))}
                </>
            } else if(valueFunc(lastDivisionLevelRanking) > valueFunc(previousDivisionLevelRanking)) {
                diffValueContent = <>
                    <img className="trend_up" src="/trend-green.png" alt="up" />
                    +{formatter(valueFunc(lastDivisionLevelRanking) - valueFunc(previousDivisionLevelRanking))}
                </>
            } else {
                diffValueContent = <>
                    <img src="/trend-gray.png" alt="same" />
                    +0
                </>
            }

            if(positionFunc(previousDivisionLevelRanking) > positionFunc(lastDivisionLevelRanking)) {
                divisionLevelDiffPositionContent = <>
                    <img className="trend_up" src="/trend-green.png" alt="up" />
                    {positionFunc(lastDivisionLevelRanking) - positionFunc(previousDivisionLevelRanking)}
                </>
            } else if(positionFunc(lastDivisionLevelRanking) > positionFunc(previousDivisionLevelRanking)) {
                divisionLevelDiffPositionContent = <>
                    <img className="trend_down" src="/trend-red.png" alt="down" />
                    +{positionFunc(lastDivisionLevelRanking) - positionFunc(previousDivisionLevelRanking)}
                </>
            } else {
                divisionLevelDiffPositionContent = <>
                    <img src="/trend-gray.png" alt="same" />
                    +0
                </>
            }
        }

        let leagueDiffPositionContent: JSX.Element
        if(previousLeagueRanking) {
            if(positionFunc(previousLeagueRanking) > positionFunc(lastLeagueRanking)) {
                leagueDiffPositionContent = <>
                    <img className="trend_up" src="/trend-green.png" alt="up"/>
                    {(positionFunc(lastLeagueRanking) - positionFunc(previousLeagueRanking))}
                </>
            } else if (positionFunc(previousLeagueRanking) < positionFunc(lastLeagueRanking)) {
                leagueDiffPositionContent = <>
                    <img className="trend_down" src="/trend-red.png" alt="down"/>
                    +{positionFunc(lastLeagueRanking) - positionFunc(previousLeagueRanking)}
                </>
            } else {
                leagueDiffPositionContent = <>
                    <img src="/trend-gray.png" alt="same" />+0
                </>
            }
        }


            return <Translation>{
                (t, { i18n }) => 
        <span className="ranking">
            <span className="ranking_name">{this.props.title}</span>
            <table className="ranking_table">
                <tbody>
                <tr className="ranking_row">
                    <td className="ranking_row_name">{this.props.title}</td>
                    <td className="ranking_row_value">{formatter(valueFunc(lastDivisionLevelRanking))}</td>
                    <td className="ranking_row_diff">
                        <div className="ranking_row_diff_value">
                            {diffValueContent}
                        </div>
                    </td>
                </tr>
                <tr className="ranking_row">
                    <td className="ranking_row_name">
                        <LeagueLink  tableLink={true}
                            id={this.props.rankingData.teamLevelDataProps.leagueId()}
                            text={this.props.rankingData.teamLevelDataProps.levelData.leagueName}
                            />
                    </td>
                    <td className="ranking_row_value">
                        <LeagueLink id={this.props.rankingData.teamLevelDataProps.leagueId()} 
                            tableLink={true}
                            text={(positionFunc(lastLeagueRanking) + 1).toString()} 
                            page={this.props.page} 
                            sortingField={this.props.sortingField} 
                            rowNumber={positionFunc(lastLeagueRanking)}
                            round={this.props.rankingData.teamLevelDataProps.currentRound()}
                        />
                        /{this.props.rankingData.leagueTeamsCount}
                    </td>
                    <td className="ranking_row_diff">
                        {(previousLeagueRanking) ? <LeagueLink id={this.props.rankingData.teamLevelDataProps.leagueId()} 
                            tableLink={true}
                            text={leagueDiffPositionContent} 
                            page={this.props.page}
                            sortingField={this.props.sortingField}  
                            rowNumber={positionFunc(previousLeagueRanking)} 
                            round={this.props.rankingData.teamLevelDataProps.currentRound() - 1}    
                        /> : <></>
                        }
                    </td>
                </tr>
                <tr className="ranking_row">
                    <td className="ranking_row_name">
                        <DivisionLevelLink
                            leagueId={this.props.rankingData.teamLevelDataProps.leagueId()}
                            divisionLevel={this.props.rankingData.teamLevelDataProps.levelData.divisionLevel}
                            text={this.props.rankingData.teamLevelDataProps.levelData.divisionLevelName}
                            />
                    </td>
                    <td className="ranking_row_value">
                        <DivisionLevelLink 
                            leagueId={this.props.rankingData.teamLevelDataProps.leagueId()}
                            divisionLevel={this.props.rankingData.teamLevelDataProps.levelData.divisionLevel}
                            text={(positionFunc(lastDivisionLevelRanking) + 1).toString()}
                            page={this.props.page}
                            sortingField={this.props.sortingField} 
                            rowNumber={positionFunc(lastDivisionLevelRanking)}
                            round={this.props.rankingData.teamLevelDataProps.currentRound()}
                        />/{this.props.rankingData.divisionLevelTeamsCount}
                    </td>
                    <td className="ranking_row_diff">
                    {(previousDivisionLevelRanking) ? <DivisionLevelLink 
                            leagueId={this.props.rankingData.teamLevelDataProps.leagueId()}
                            divisionLevel={this.props.rankingData.teamLevelDataProps.levelData.divisionLevel}
                            text={divisionLevelDiffPositionContent}
                            page={this.props.page}
                            sortingField={this.props.sortingField} 
                            rowNumber={positionFunc(previousDivisionLevelRanking)}
                            round={this.props.rankingData.teamLevelDataProps.currentRound() - 1}
                        /> : <></>
                    }
                    </td>
                </tr>
                </tbody>
            </table>
        </span>
        }
        </Translation>
    }
}

export default RankingTable