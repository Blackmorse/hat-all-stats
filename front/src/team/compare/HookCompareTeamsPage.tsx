import React from 'react'
import TeamLevelDataProps from '../TeamLevelDataProps'
import { LevelDataPropsWrapper } from "../../common/LevelDataProps";
import ExecutableComponent, {StateAndRequest} from "../../common/sections/HookExecutableComponent"
import TeamComparsion from '../../rest/models/team/TeamComparsion';
import { LoadingEnum } from '../../common/enums/LoadingEnum';
import { getTeamsComparsion } from '../../rest/Client'
import {useTranslation} from "react-i18next"
import { Col, Row, Card } from 'react-bootstrap';
import RankingParametersProvider from '../../common/ranking/RankingParametersProvider'
import CompareTeamsTable from './CompareTeamsTable'
import { PagesEnum } from '../../common/enums/PagesEnum';

const CompareTeamsPage = (props: LevelDataPropsWrapper<TeamLevelDataProps>) => {
    let params = new URLSearchParams(window.location.search);
    let teamId = (params.get('teamId') === null) ? undefined : Number(params.get('teamId'))

    const [t, _i18n] = useTranslation() 

    const content = (stateAndRequest: StateAndRequest<number | undefined, TeamComparsion | undefined>) => {
        if (stateAndRequest.currentState === undefined) return <></>

        if (stateAndRequest.currentState.team1Rankings.length === 0 ||
                stateAndRequest.currentState.team2Rankings.length === 0) {
            return <>{t('team.unable_to_compare')}</>
        }
              
        let teamComparsion = stateAndRequest.currentState
        return <Card className='mt-3 shadow'> 
        <Card.Header className='lead'>{t(PagesEnum.TEAM_COMPARSION)}</Card.Header>
        <Card.Body>
            <Row>
            <Col lg={6}>
                <CompareTeamsTable 
                    teamComparsion={teamComparsion}
                    teamLevelDataProps={props.levelDataProps} 
                    rankingParameters={RankingParametersProvider.HATSTATS()}
                />
            </Col>
            <Col lg={6}>
                <CompareTeamsTable 
                    teamComparsion={teamComparsion}
                    teamLevelDataProps={props.levelDataProps} 
                    rankingParameters={RankingParametersProvider.SALARY(props.levelDataProps.currencyRate(), props.levelDataProps.currency())}
                />
            </Col>
            <Col lg={6}>
                <CompareTeamsTable 
                    teamComparsion={teamComparsion}
                    teamLevelDataProps={props.levelDataProps} 
                    rankingParameters={RankingParametersProvider.TSI()}
                />
            </Col>
            <Col lg={6}>
                <CompareTeamsTable 
                    teamComparsion={teamComparsion}
                    teamLevelDataProps={props.levelDataProps} 
                    rankingParameters={RankingParametersProvider.ATTACK()}
                />
            </Col>
            <Col lg={6}>
                <CompareTeamsTable 
                    teamComparsion={teamComparsion}
                    teamLevelDataProps={props.levelDataProps} 
                    rankingParameters={RankingParametersProvider.DEFENSE()}
                />
            </Col>
            <Col lg={6}>
                <CompareTeamsTable 
                    teamComparsion={teamComparsion}
                    teamLevelDataProps={props.levelDataProps} 
                    rankingParameters={RankingParametersProvider.MIDFIELD()}
                />
            </Col>
            <Col lg={6}>
                <CompareTeamsTable 
                    teamComparsion={teamComparsion}
                    teamLevelDataProps={props.levelDataProps} 
                    rankingParameters={RankingParametersProvider.AGE()}
                />
            </Col>
            <Col lg={6}>
                <CompareTeamsTable 
                    teamComparsion={teamComparsion}
                    teamLevelDataProps={props.levelDataProps} 
                    rankingParameters={RankingParametersProvider.RATING()}
                    diffFormatter={value => <>{value / 10}</>}
                />
            </Col>
            <Col lg={6}>
                <CompareTeamsTable 
                    teamComparsion={teamComparsion}
                    teamLevelDataProps={props.levelDataProps} 
                    rankingParameters={RankingParametersProvider.RATING_END_OF_MATCH()}
                    diffFormatter={value => <>{value / 10}</>}
                />
            </Col>
            <Col lg={6}>
                <CompareTeamsTable 
                    teamComparsion={teamComparsion}
                    teamLevelDataProps={props.levelDataProps} 
                    rankingParameters={RankingParametersProvider.POWER_RATINGS()}
                />
            </Col>
            <Col lg={6}>
                <CompareTeamsTable 
                    teamComparsion={teamComparsion}
                    teamLevelDataProps={props.levelDataProps} 
                    rankingParameters={RankingParametersProvider.INJURY()}
                />
            </Col>
            <Col lg={6}>
                <CompareTeamsTable 
                    teamComparsion={teamComparsion}
                    teamLevelDataProps={props.levelDataProps} 
                    rankingParameters={RankingParametersProvider.INJURY_COUNT()}
                />
            </Col>
            <Col lg={6}>
                <CompareTeamsTable 
                    teamComparsion={teamComparsion}
                    teamLevelDataProps={props.levelDataProps} 
                    rankingParameters={RankingParametersProvider.LODDAR_STATS()}
                />
            </Col>
            <Col lg={6}>
                <CompareTeamsTable 
                    teamComparsion={teamComparsion}
                    teamLevelDataProps={props.levelDataProps} 
                    rankingParameters={RankingParametersProvider.FOUNDED_DATE()}
                />
            </Col>
        </Row>
        </Card.Body>
        </Card>
    }

    return <ExecutableComponent<number | undefined, TeamComparsion | undefined>
        executeRequest={(request: number | undefined, callback: (loadingState: LoadingEnum, result?: TeamComparsion) => void) => {
            if (request !== undefined) {
                getTeamsComparsion(props.levelDataProps.teamId(), request, callback)
            } else {
                callback(LoadingEnum.OK)
            }
        }}
        responseToState={response => response}
        initialRequest={teamId}
        content={content}
    />
}

export default CompareTeamsPage
