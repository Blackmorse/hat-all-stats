import React from 'react'
import LeagueUnitData from '../rest/models/leveldata/LeagueUnitData'
import '../common/menu/TopMenu.css'
import TopMenu from '../common/menu/TopMenu';
import ExternalLeagueUnitLink from '../common/links/ExternalLeagueUnitLink';

interface Props {
    leagueUnitData?: LeagueUnitData,
    callback: (teamId: number) => void
}

class LeagueUnitTopMenu extends TopMenu<Props> {

    onChanged = (event: React.FormEvent<HTMLSelectElement>) => {
        this.props.callback(Number(event.currentTarget.value))
    }

    links(): [string, string?][] {
        return [
            ["/league/" + this.props.leagueUnitData?.leagueId, this.props.leagueUnitData?.leagueName],
            ["/league/" + this.props.leagueUnitData?.leagueId + "/divisionLevel/" + this.props.leagueUnitData?.divisionLevel, this.props.leagueUnitData?.divisionLevelName],
            ["/leagueUnit/" + this.props.leagueUnitData?.leagueUnitId, this.props.leagueUnitData?.leagueUnitName]
        ]
    }

    externalLink(): JSX.Element | undefined {
        return <ExternalLeagueUnitLink id={this.props.leagueUnitData?.leagueUnitId || 0} black={false} />
    }

    selectBox(): JSX.Element {
        return <select className="href_select" onChange={this.onChanged}>
                <option value={undefined}>Select...</option>
                {this.props.leagueUnitData?.teams.map(([teamId, teamName]) => {
                    return <option key={'league_unit_top_option_' + teamId} value={teamId}>{teamName}</option>
                })}
            </select>
    }
}

export default LeagueUnitTopMenu