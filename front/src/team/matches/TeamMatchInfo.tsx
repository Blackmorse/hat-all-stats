import React from 'react'
import TeamMatchRatingsTable from './TeamMatchRatingsTable'
import TeamLink from '../../common/links/TeamLink'
import { Translation } from 'react-i18next'
import '../../i18n'
import TeamMatchMetaInfo from './TeamMatchMetaInfo'
import ExternalMatchLink from '../../common/links/ExternalMatchLink'
import SingleMatch from '../../rest/models/match/SingleMatch'
import MatchSimulatorInfo from './MatchSimulatorInfo'
import { SectionState } from '../../common/sections/Section'
import { Col, Container, Row } from 'react-bootstrap'

interface Props {
    singleMatch: SingleMatch,
    hideSimulator?: boolean
}

class TeamMatchInfo extends React.Component<Props, SectionState> {
    constructor(props: Props) {
        super(props)
        this.state = {collapsed: false}
    }

    render() {
        let singleMatch = this.props.singleMatch
        
        return  <Translation>{
            (t, { i18n }) => <div>
            <Row className='mb-2'>
                <Col lg={5} className='text-center'>
                    <TeamLink id={singleMatch.homeTeamId} text={singleMatch.homeTeamName} forceRefresh={true}/>
                </Col>
                <Col lg={2} className='text-center'>
                    {singleMatch.homeGoals} - {singleMatch.awayGoals} 
                    {(singleMatch.matchId !== undefined) ? <ExternalMatchLink id={singleMatch.matchId} /> : <></>}
                </Col>
                <Col lg={5} className='text-center'>
                    <TeamLink id={singleMatch.awayTeamId} text={singleMatch.awayTeamName} forceRefresh={true}/>
                </Col>
            </Row>
            <Container d-flex className='d-flex justify-content-center'>    
                {(this.props.hideSimulator) ? <></> : <MatchSimulatorInfo singleMatch={singleMatch}/>}
            </Container>
            <Row className='d-flex flex-row justify-content-around align-items-center'>
                <Col lg={3} md={12} className='d-flex justify-content-center'>
                    <TeamMatchMetaInfo matchRatings={singleMatch.homeMatchRatings}/>                  
                </Col>
                <Col lg={6} md={12} className='d-flex justify-content-center'>
                    <TeamMatchRatingsTable homeMatchRatings={singleMatch.homeMatchRatings} awayMatchRatings={singleMatch.awayMatchRatings} />
                </Col>
                <Col lg={3} md={12} className='d-flex justify-content-center'>
                    <TeamMatchMetaInfo matchRatings={singleMatch.awayMatchRatings}/>
                </Col>
            </Row>
        </div>
        }
        </Translation>
    }
}

export default TeamMatchInfo