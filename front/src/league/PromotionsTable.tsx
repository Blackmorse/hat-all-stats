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
        this.updateCurrent=this.updateCurrent.bind(this)
    }


    componentDidMount() {
        this.setState({
            promotions: this.state.promotions,
            dataLoading: true,
            isError: false
        })

        getPromotions(this.props.levelDataProps.createLevelRequest(),
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
                return <React.Fragment key={'promotions_table_entry' + promotionWithType.upDivisionLevelName + '_' + promotionWithType.downDivisionLevelName + '_' + promotionWithType.promoteType}>
                    <span className="promotion_type">
                        <DivisionLevelLink leagueId={this.props.levelDataProps.leagueId()} divisionLevel={promotionWithType.upDivisionLevel} text={promotionWithType.upDivisionLevelName + ' '}/>
                        ↔
                        <DivisionLevelLink leagueId={this.props.levelDataProps.leagueId()} divisionLevel={promotionWithType.upDivisionLevel + 1} text={' ' + promotionWithType.downDivisionLevelName}/>
                        {(promotionWithType.promoteType === "auto") ? ' ' + t('promotions.auto_promotions') : ' ' + t('promotions.qualifications')}
                    </span>
                    <table className="promotions_table">
                        <tbody>
                        {promotionWithType.promotions.map(promotion => {
                            return <tr className="promotion_row" key={'promotion_row_' + promotion.promoteType + '_' + promotion.upDivisionLevel + '_' + promotion.downTeams.map(dt => dt.teamId).join('_')}>
                            <td className="promotion_teams">
                                <table className="promotion_entry_table">
                                    <tbody>
                                    {promotion.downTeams.map(downTeam => {
                                        return <tr key={'promotions_down_teams_team' + downTeam.teamId}>
                                        <td className="promotion_team_name">
                                            <TeamLink id={downTeam.teamId} text={downTeam.teamName} />
                                        </td>
                                        <td className="promotion_league_unit_name">
                                            <LeagueUnitLink id={downTeam.leagueUnitId} text={downTeam.leagueUnitName} />
                                        </td>
                                    </tr>
                                    })}
                                    </tbody>
                                    </table>
                                </td>
                                <td className="promotions_separator">-</td>
                                <td className="promotion_teams">
                                    <table className="promotion_entry_table">
                                        <tbody>
                                        {promotion.upTeams.map(upTeam => {
                                            return <tr key={'promotions_up_team_team_' + upTeam.teamId}>
                                                <td className="promotion_league_unit_name">
                                                    <LeagueUnitLink id={upTeam.leagueUnitId} text={upTeam.leagueUnitName} />
                                                </td>
                                                <td className="promotion_team_name">
                                                    <TeamLink id={upTeam.teamId} text={upTeam.teamName} />
                                                </td>
                                            </tr>
                                        })}
                                        </tbody>
                                        </table>
                                    </td>
                                </tr>
                        })}
                        </tbody>
                    </table>
                </React.Fragment>
            })}
        </div>
        }
        </Translation>
    }

}

export default PromotionsTable