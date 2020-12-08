import React from 'react'
import DreamTeamPlayer from '../../../rest/models/player/DreamTeamPlayer';
import './DreamTeamPlayerCard.css'
import { ratingFormatter } from '../../Formatters'
import ExternalPlayerLink from '../../links/ExternalPlayerLink';
import TeamLink from '../../links/TeamLink';
import LeagueUnitLink from '../../links/LeagueUnitLink';

interface Props {
    position: string,
    player?: DreamTeamPlayer
}

class DreamTeamPlayerCard extends React.Component<Props> {
    render() {
        let content = <></>
        if (this.props.player !== undefined) {
            content = <>
                <span className="player_name player_card_line">
                    {this.props.player?.playerSortingKey.firstName} {this.props.player.playerSortingKey.lastName}{<ExternalPlayerLink id={this.props.player?.playerSortingKey.playerId}/>}
                </span>
                <span className="player_team player_card_line">
                    <TeamLink id={this.props.player.playerSortingKey.teamId} text={this.props.player.playerSortingKey.teamName} />
                </span>   
                <span className="player_league_unit player_card_line">
                    <LeagueUnitLink text={this.props.player.playerSortingKey.leagueUnitName} id={this.props.player.playerSortingKey.leagueUnitId} />
                </span>
                <span className="player_rating player_card_line">{ratingFormatter(this.props.player.rating)}</span>
            </>
        }
        return <div className="player_card">
            <span className="player_card_header">{this.props.position}</span>
            {content}
        </div>
    }
}

export default DreamTeamPlayerCard