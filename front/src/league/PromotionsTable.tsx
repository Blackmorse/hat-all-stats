import React from 'react';
import LevelDataProps, { LevelDataPropsWrapper } from '../common/LevelDataProps'
import StatisticsSection from '../common/sections/StatisticsSection'
import LevelData from '../rest/models/leveldata/LevelData'
import { getPromotions } from '../rest/Client'
import PromotionWithType from '../rest/models/promotions/Promotion'
import { Translation } from 'react-i18next'
import '../i18n'
import './PromotionsTable.css'
import DivisionLevelLink from '../common/links/DivisionLevelLink';
import TeamLink from '../common/links/TeamLink';
import LeagueUnitLink from '../common/links/LeagueUnitLink';

interface State {
    dataLoading: boolean,
    isError: boolean,
    promotions?: Array<PromotionWithType>
}

class PromotionsTable<Data extends LevelData, Props extends LevelDataProps<Data>> extends StatisticsSection<LevelDataPropsWrapper<Data, LevelDataProps<Data>>, State> {
    constructor(props: LevelDataPropsWrapper<Data, Props>) {
        super(props, 'menu.promotions')
        this.state = {
            dataLoading: false,
            isError: false
        }
    }


    componentDidMount() {
        this.setState({
            promotions: this.state.promotions,
            dataLoading: true,
            isError: false
        })

        getPromotions(this.props.levelDataProps.leagueId(),
            promotions => this.setState({
                promotions: promotions,
                dataLoading: false,
                isError: false
            }),
            () => this.setState({
                promotions: this.state.promotions,
                dataLoading: false,
                isError: true
            }))
    }

    updateCurrent(): void {
        this.componentDidMount()
    }

    renderSection(): JSX.Element {
        return <Translation>
            {(t, { i18n }) =>
        <div className="promotions_content">
            {this.state.promotions?.map(promotionWithType => {
                return <>
                    <span className="promotion_type">
                        <DivisionLevelLink leagueId={this.props.levelDataProps.leagueId()} divisionLevel={promotionWithType.upDivisionLevel} text={promotionWithType.upDivisionLevelName + ' '}/>
                        ↔
                        <DivisionLevelLink leagueId={this.props.levelDataProps.leagueId()} divisionLevel={promotionWithType.upDivisionLevel + 1} text={' ' + promotionWithType.downDivisionLevelName}/>
                        {(promotionWithType.promoteType === "auto") ? ' ' + t('promotions.auto_promotions') : ' ' + t('promotions.qualifications')}
                    </span>
                    <table className="promotions_table">
                        <tbody>
                        {promotionWithType.promotions.map(promotion => {
                            return <tr className="promotion_row">
                            <td className="promotion_teams">
                                <table className="promotion_entry_table">
                                    {promotion.downTeams.map(downTeam => {
                                        return <tr>
                                        <td className="promotion_team_name">
                                            <TeamLink id={downTeam.teamId} text={downTeam.teamName} />
                                        </td>
                                        <td className="promotion_league_unit_name">
                                            <LeagueUnitLink id={downTeam.leagueUnitId} text={downTeam.leagueUnitName} />
                                        </td>
                                    </tr>
                                    })}
                                    </table>
                                </td>
                                <td className="promotions_separator">-</td>
                                <td className="promotion_teams">
                                    <table className="promotion_entry_table">
                                        {promotion.upTeams.map(upTeam => {
                                            return <tr>
                                                <td className="promotion_league_unit_name">
                                                    <LeagueUnitLink id={upTeam.leagueUnitId} text={upTeam.leagueUnitName} />
                                                </td>
                                                <td className="promotion_team_name">
                                                    <TeamLink id={upTeam.teamId} text={upTeam.teamName} />
                                                </td>
                                            </tr>
                                        })}
                                        </table>
                                    </td>
                                </tr>
                        })}
                        </tbody>
                    </table>
                </>
            })}
        </div>
        }
        </Translation>
    }

}

export default PromotionsTable