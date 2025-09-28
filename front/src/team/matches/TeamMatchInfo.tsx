import {Col, Container, Row} from 'react-bootstrap'
import ExternalMatchLink from '../../common/links/ExternalMatchLink'
import TeamLink from '../../common/links/TeamLink'
import SingleMatch from '../../rest/models/match/SingleMatch'
import MatchSimulatorInfo from './MatchSimulatorInfo'
import TeamMatchMetaInfo from './TeamMatchMetaInfo'
import TeamMatchRatingsTable from './TeamMatchRatingsTable'

interface Props {
    singleMatch: SingleMatch,
    hideSimulator?: boolean
}

const TeamMatchInfo = (props: Props) => {

    const singleMatch = props.singleMatch
    
    return  <div>
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
            {(props.hideSimulator) ? <></> : <MatchSimulatorInfo singleMatch={singleMatch}/>}
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

export default TeamMatchInfo
