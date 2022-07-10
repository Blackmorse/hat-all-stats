import React from 'react';
import TeamComparsion from '../../rest/models/team/TeamComparsion';
import { LevelDataPropsWrapper } from '../../common/LevelDataProps'
import TeamLevelDataProps from '../TeamLevelDataProps'
import { LoadingEnum } from '../../common/enums/LoadingEnum';
import { getTeamsComparsion } from '../../rest/Client'
import CompareTeamsTable from './CompareTeamsTable'
import '../../i18n'
import RankingParametersProvider from '../../common/ranking/RankingParametersProvider'
import ExecutableComponent from '../../common/sections/ExecutableComponent';
import { SectionState } from '../../common/sections/Section';
import i18n from '../../i18n';
import { Col, Row } from 'react-bootstrap';

interface State {
    teamComparsion?: TeamComparsion
}

class CompareTeamsPage extends ExecutableComponent<LevelDataPropsWrapper<TeamLevelDataProps>, 
    State & SectionState, TeamComparsion, number | undefined> {
    
        constructor(props: LevelDataPropsWrapper<TeamLevelDataProps>) {
        super(props)
        
        this.state = {
            loadingState: LoadingEnum.OK,
            dataRequest: props.queryParams.teamId,
            collapsed: false
        }
    }
    
    executeDataRequest(dataRequest: number | undefined, 
            callback: (loadingState: LoadingEnum, result?: TeamComparsion) => void): void {
        if(dataRequest !== undefined) {
            getTeamsComparsion(this.props.levelDataProps.teamId(), dataRequest, callback)
        } else {
            callback(LoadingEnum.OK)
        }
    }

    stateFromResult(result?: TeamComparsion): State & SectionState {
        return {
            teamComparsion: result,
            collapsed: this.state.collapsed
        }
    }

    renderSection(): JSX.Element {
        if (this.state.teamComparsion === undefined) {
            return <></>
        }

        if (this.state.teamComparsion.team1Rankings.length === 0 ||
                this.state.teamComparsion.team2Rankings.length === 0) {
            return <>{i18n.t('team.unable_to_compare')}</>
        }
              
        let teamComparsion = this.state.teamComparsion
        return <Row>
            <Col lg={6}>
                <CompareTeamsTable 
                    teamComparsion={teamComparsion}
                    teamLevelDataProps={this.props.levelDataProps} 
                    rankingParameters={RankingParametersProvider.HATSTATS()}
                />
            </Col>
            <Col lg={6}>
                <CompareTeamsTable 
                    teamComparsion={teamComparsion}
                    teamLevelDataProps={this.props.levelDataProps} 
                    rankingParameters={RankingParametersProvider.SALARY(this.props.levelDataProps.currencyRate(), this.props.levelDataProps.currency())}
                />
            </Col>
            <Col lg={6}>
                <CompareTeamsTable 
                    teamComparsion={teamComparsion}
                    teamLevelDataProps={this.props.levelDataProps} 
                    rankingParameters={RankingParametersProvider.TSI()}
                />
            </Col>
            <Col lg={6}>
                <CompareTeamsTable 
                    teamComparsion={teamComparsion}
                    teamLevelDataProps={this.props.levelDataProps} 
                    rankingParameters={RankingParametersProvider.ATTACK()}
                />
            </Col>
            <Col lg={6}>
                <CompareTeamsTable 
                    teamComparsion={teamComparsion}
                    teamLevelDataProps={this.props.levelDataProps} 
                    rankingParameters={RankingParametersProvider.DEFENSE()}
                />
            </Col>
            <Col lg={6}>
                <CompareTeamsTable 
                    teamComparsion={teamComparsion}
                    teamLevelDataProps={this.props.levelDataProps} 
                    rankingParameters={RankingParametersProvider.MIDFIELD()}
                />
            </Col>
            <Col lg={6}>
                <CompareTeamsTable 
                    teamComparsion={teamComparsion}
                    teamLevelDataProps={this.props.levelDataProps} 
                    rankingParameters={RankingParametersProvider.AGE()}
                />
            </Col>
            <Col lg={6}>
                <CompareTeamsTable 
                    teamComparsion={teamComparsion}
                    teamLevelDataProps={this.props.levelDataProps} 
                    rankingParameters={RankingParametersProvider.RATING()}
                    diffFormatter={value => <>{value / 10}</>}
                />
            </Col>
            <Col lg={6}>
                <CompareTeamsTable 
                    teamComparsion={teamComparsion}
                    teamLevelDataProps={this.props.levelDataProps} 
                    rankingParameters={RankingParametersProvider.RATING_END_OF_MATCH()}
                    diffFormatter={value => <>{value / 10}</>}
                />
            </Col>
            <Col lg={6}>
                <CompareTeamsTable 
                    teamComparsion={teamComparsion}
                    teamLevelDataProps={this.props.levelDataProps} 
                    rankingParameters={RankingParametersProvider.POWER_RATINGS()}
                />
            </Col>
            <Col lg={6}>
                <CompareTeamsTable 
                    teamComparsion={teamComparsion}
                    teamLevelDataProps={this.props.levelDataProps} 
                    rankingParameters={RankingParametersProvider.INJURY()}
                />
            </Col>
            <Col lg={6}>
                <CompareTeamsTable 
                    teamComparsion={teamComparsion}
                    teamLevelDataProps={this.props.levelDataProps} 
                    rankingParameters={RankingParametersProvider.INJURY_COUNT()}
                />
            </Col>
            <Col lg={6}>
                <CompareTeamsTable 
                    teamComparsion={teamComparsion}
                    teamLevelDataProps={this.props.levelDataProps} 
                    rankingParameters={RankingParametersProvider.LODDAR_STATS()}
                />
            </Col>
            <Col lg={6}>
                <CompareTeamsTable 
                    teamComparsion={teamComparsion}
                    teamLevelDataProps={this.props.levelDataProps} 
                    rankingParameters={RankingParametersProvider.FOUNDED_DATE()}
                />
            </Col>
            </Row>
    }
}

export default CompareTeamsPage
