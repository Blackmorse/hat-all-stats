import React from 'react'
import LeagueUnitData from '../rest/models/leveldata/LeagueUnitData'
import '../common/menu/TopMenu.css'
import TopMenu from '../common/menu/TopMenu';
import ExternalLeagueUnitLink from '../common/links/ExternalLeagueUnitLink';
import { Form } from 'react-bootstrap';

interface Props {
    data?: LeagueUnitData,
    callback: (teamId: number) => void
}

class LeagueUnitTopMenu extends TopMenu<LeagueUnitData, Props> {

    onChanged = (event: React.FormEvent<HTMLSelectElement>) => {
        this.props.callback(Number(event.currentTarget.value))
    }

    links(): [string, string?][] {
        return [
            ["/league/" + this.props.data?.leagueId, this.props.data?.leagueName],
            ["/league/" + this.props.data?.leagueId + "/divisionLevel/" + this.props.data?.divisionLevel, this.props.data?.divisionLevelName],
            ["/leagueUnit/" + this.props.data?.leagueUnitId, this.props.data?.leagueUnitName]
        ]
    }

    externalLink(): JSX.Element | undefined {
        return <ExternalLeagueUnitLink id={this.props.data?.leagueUnitId || 0} black={false} />
    }

    selectBox(): JSX.Element {
        return <Form>
            <Form.Select  size="sm" className="mt-3 mb-3 pr-3" max-width="200" onChange={this.onChanged}>
                <option value={undefined}>Select...</option>
                {this.props.data?.teams.map(([teamId, teamName]) => {
                    return <option key={'league_unit_top_option_' + teamId} value={teamId}>{teamName}</option>
                })}
            </Form.Select>
        </Form>
    }
}

export default LeagueUnitTopMenu