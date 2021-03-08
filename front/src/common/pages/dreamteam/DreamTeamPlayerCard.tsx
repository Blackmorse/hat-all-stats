import React from 'react'
import './DreamTeamPlayerCard.css'
import { ratingFormatter } from '../../Formatters'
import ExternalPlayerLink from '../../links/ExternalPlayerLink';
import TeamLink from '../../links/TeamLink';
import LeagueUnitLink from '../../links/LeagueUnitLink';
import CountryImage from '../../elements/CountryImage';
import LeagueLink from '../../links/LeagueLink';
import { DreamTeamPlayerPosition } from '../DreamTeamPage'

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
                <span className="player_name player_card_line">
                    {this.props.dreamTeamPlayerPosition.player?.playerSortingKey.firstName} {this.props.dreamTeamPlayerPosition.player.playerSortingKey.lastName}
                    {<ExternalPlayerLink id={this.props.dreamTeamPlayerPosition.player?.playerSortingKey.playerId}/>}
                </span>
                <span className="player_nationality player_card_line">
                    <LeagueLink id={this.props.dreamTeamPlayerPosition.player.playerSortingKey.nationality} forceRefresh={true}
                        text={<CountryImage countryId={this.props.dreamTeamPlayerPosition.player.playerSortingKey.nationality} />}/>  
                </span>
                <span className="player_team player_card_line">
                    <TeamLink id={this.props.dreamTeamPlayerPosition.player.playerSortingKey.teamId} 
                        text={this.props.dreamTeamPlayerPosition.player.playerSortingKey.teamName} 
                        flagCountryNumber={this.props.showTeamCountryFlag !== undefined && this.props.showTeamCountryFlag ? this.props.dreamTeamPlayerPosition.player.playerSortingKey.teamLeagueId : undefined}/>
                </span>   
                <span className="player_league_unit player_card_line">
                    <LeagueUnitLink text={this.props.dreamTeamPlayerPosition.player.playerSortingKey.leagueUnitName} 
                        id={this.props.dreamTeamPlayerPosition.player.playerSortingKey.leagueUnitId} />
                </span>
                <span className="player_rating player_card_line">{ratingFormatter(this.props.dreamTeamPlayerPosition.player.rating)}</span>
            </>
        }
        
        return <div className="player_card">
            <span className="player_card_header">{this.props.dreamTeamPlayerPosition.position}</span>
            {content}
        </div>
    }
}

export default DreamTeamPlayerCard