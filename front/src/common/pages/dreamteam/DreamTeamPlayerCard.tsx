import React from 'react'
import { ratingFormatter } from '../../Formatters'
import ExternalPlayerLink from '../../links/ExternalPlayerLink';
import TeamLink from '../../links/TeamLink';
import LeagueUnitLink from '../../links/LeagueUnitLink';
import CountryImage from '../../elements/CountryImage';
import LeagueLink from '../../links/LeagueLink';
import { DreamTeamPlayerPosition } from '../DreamTeamPage'
import { Card } from 'react-bootstrap';

interface Props {
    dreamTeamPlayerPosition?: DreamTeamPlayerPosition,
    showTeamCountryFlag?: boolean
}

class DreamTeamPlayerCard extends React.Component<Props> {
    render() {

        if(this.props.dreamTeamPlayerPosition === undefined) {
            return <></>
        }
        let content = <></>
        
        if (this.props.dreamTeamPlayerPosition.player !== undefined) {
            content = <>
                <span className='text-center'>
                    <LeagueLink id={this.props.dreamTeamPlayerPosition.player.playerSortingKey.nationality} forceRefresh={true}
                        text={<CountryImage countryId={this.props.dreamTeamPlayerPosition.player.playerSortingKey.nationality} />}/>  
                </span>
                <span className='text-center'>
                    {this.props.dreamTeamPlayerPosition.player?.playerSortingKey.firstName} {this.props.dreamTeamPlayerPosition.player.playerSortingKey.lastName}
                    {<ExternalPlayerLink id={this.props.dreamTeamPlayerPosition.player?.playerSortingKey.playerId}/>}
                </span>
                <span className='text-center'>
                    <TeamLink id={this.props.dreamTeamPlayerPosition.player.playerSortingKey.teamId} 
                        text={this.props.dreamTeamPlayerPosition.player.playerSortingKey.teamName} 
                        flagCountryNumber={this.props.showTeamCountryFlag !== undefined && this.props.showTeamCountryFlag ? this.props.dreamTeamPlayerPosition.player.playerSortingKey.teamLeagueId : undefined}/>
                </span>   
                <span className='text-center'>
                    <LeagueUnitLink text={this.props.dreamTeamPlayerPosition.player.playerSortingKey.leagueUnitName} 
                        id={this.props.dreamTeamPlayerPosition.player.playerSortingKey.leagueUnitId} />
                </span>
                <span className='text-center'>{ratingFormatter(this.props.dreamTeamPlayerPosition.player.rating)}</span>
            </>
        }
        
        return <Card className='h-100 small-font shadow-sm mx-1'>
            <Card.Header className='text-center'>{this.props.dreamTeamPlayerPosition.position}</Card.Header>
            <Card.Body className='d-flex flex-column justify-content-center'>{content}</Card.Body>
        </Card>
    }
}

export default DreamTeamPlayerCard