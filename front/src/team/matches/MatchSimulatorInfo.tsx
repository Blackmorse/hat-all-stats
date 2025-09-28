import React from 'react'
import { Col, Container, Row } from 'react-bootstrap'
import { useTranslation } from 'react-i18next'
import { LoadingEnum } from '../../common/enums/LoadingEnum'
import ExecutableComponent, { StateAndRequest } from '../../common/sections/HookExecutableComponent'
import CheckBoxSelector from '../../common/selectors/CheckBoxSelector'
import { getSimilarMatchesByRatingsWithAnnoy, SimilarMatchesRequest } from '../../rest/clients/MatchStatsClient'
import SimilarMatchesStats from '../../rest/models/match/SimilarMatchesStats'
import SingleMatch from '../../rest/models/match/SingleMatch'
import './MatchSimulatorInfo.css'
import { Slider } from '@mui/material'


const MatchSimulatorInfo = (props: {singleMatch: SingleMatch}) => {
    const [ accuracy, setAccuracy ] = React.useState(5)
    const [ considerTacticType, setConsiderTacticType ] = React.useState(false)
    const [ considerTacticSkill, setConsiderTacticSkill ] = React.useState(false)
    const [ considerSetPiecesLevels, setConsiderSetPiecesLevels ] = React.useState(false)

    const [ t, _i18n ] = useTranslation()

    const createRequest = () => {
        return {
            singleMatch: props.singleMatch,
            accuracy: accuracy,
            considerTacticType: considerTacticType,
            considerTacticSkill: considerTacticSkill,
            considerSetPiecesLevels: considerSetPiecesLevels
        }
    }

    const changeConsiderTacticType =(value: boolean) => {
        if (value) {
            setConsiderTacticType(true)
        } else {
            setConsiderTacticType(false)
            setConsiderTacticSkill(false)
        }
    }

    const resultsContent = (stats: SimilarMatchesStats) => {
        let homeRate = Math.floor(100 * stats.wins / (stats.wins + stats.draws + stats.loses))
        let drawRate = Math.floor(100 * stats.draws / (stats.wins + stats.draws + stats.loses))
        let awayRate = Math.floor(100 * stats.loses / (stats.wins + stats.draws + stats.loses))
        if (stats.wins + stats.draws + stats.loses === 0) {
            homeRate = 33
            drawRate = 33
            awayRate = 33
        }
        return <>
            <Container className='d-flex justify-content-center'>
                <span className='home_team_span result_span' style={{width: homeRate.toString() + '%'}}>
                    {stats.wins}
                </span> 
                <span className='draw_span result_span' style={{width: drawRate.toString() + '%'}}>
                    {stats.draws}
                </span>
                <span className='away_team_span result_span' style={{width: awayRate.toString() + '%'}}>
                    {stats.loses}
                </span>
            </Container>
            <Container className='mt-2'>
                <Row className='px-3'>
                   <Col lg={4}>{Math.round(stats.avgGoalsFor * 10) / 10}</Col> 
                   <Col className='text-center' lg={4}>{t('overview.goals')}</Col> 
                   <Col className='text-end' lg={4}>{Math.round(stats.avgGoalsAgainst * 10) / 10}</Col> 
                </Row>
            </Container>
            </>
    }

    const content = (stateAndRequest: StateAndRequest<SimilarMatchesRequest | undefined /* for lazy initialization */, SimilarMatchesStats | undefined>) => {

        let results = <></>
        const stats = stateAndRequest.currentState
        if (stats !== undefined) {
            results = resultsContent(stats)
        }

        return <div className='w-75 d-flex justify-content-center flex-column'>
            {results}
            <Container className='w-50 d-flex'>
                <Col className='text-start'>{t('match.accuracy')}:</Col>
                <Col className='w-75'>
                    <Slider aria-label={t('match.accuracy')} defaultValue={accuracy} valueLabelDisplay="auto" step={1}
                      marks min={1} max={10} 
                      onChangeCommitted={(_event, value) => setAccuracy(value as number)}
                    />
                </Col>
            </Container>
            <Container className='w-50 d-flex'>
                <Col className='text-start'>{t('match.consider_tactics_type')}:</Col>
                <Col className='w-50'>
                    <CheckBoxSelector title='' value={considerTacticType} callback={changeConsiderTacticType}/>
                </Col>
            </Container>
            {(!considerTacticType) ? <></> : <Container className='w-50 d-flex'>
                <Col className='text-start'><i className="bi bi-arrow-return-right"></i>{t('match.consider_tactics_skill')}:</Col>
                <Col className='w-50'>
                    <CheckBoxSelector title='' value={considerTacticSkill} callback={setConsiderTacticSkill}/>
                </Col>
            </Container>
            }
            <Container className='w-50 d-flex'>
                <Col className='text-start'>{t('match.consider_set_pieces')}:</Col>
                <Col className='w-50'>
                        <CheckBoxSelector title='' value={considerSetPiecesLevels} callback={setConsiderSetPiecesLevels}/>
                </Col>
            </Container>
            <button className='btn btn-success mt-2 w-25 container' onClick={() => stateAndRequest.setRequest(createRequest())}>{t('team.simulate_match')}</button>
        </div>
    }

    const executeRequest = (request: SimilarMatchesRequest | undefined, callback: (loadingEnum: LoadingEnum, result?: SimilarMatchesStats) => void) => {
        if (request === undefined) {
            callback(LoadingEnum.OK, undefined)
        } else {
            getSimilarMatchesByRatingsWithAnnoy(request, callback)
        }
    }

    return <ExecutableComponent<SimilarMatchesRequest | undefined, SimilarMatchesStats | undefined>
        executeRequest={executeRequest}
        responseToState={response => response}
        initialRequest={undefined}
        content={content}
    />
}

export default MatchSimulatorInfo
