import React from 'react';
import LeagueData from '../rest/models/leveldata/LeagueData'
import '../common/menu/TopMenu.css'
import TopMenu from '../common/menu/TopMenu'
import { toArabian } from "../common/Utils"
import ExternalLeagueLink from '../common/links/ExternalLeagueLink';
import { Form } from 'react-bootstrap';

interface Props {
    data?: LeagueData,
    callback: (divisionLevel: number) => void
}

class LeagueTopMenu extends TopMenu<LeagueData, Props> {
  onChanged = (event: React.FormEvent<HTMLSelectElement>) => {
    this.props.callback(toArabian(event.currentTarget.value))
  }

  selectBox(): JSX.Element {
        // return  <select className="href_select" onChange={this.onChanged}>
        //   <option value={undefined}>Select...</option>
        //   {this.props.leagueData?.divisionLevels.map(divisionLevel => {
        //     return <option key={'division_level_select_' + divisionLevel} value={divisionLevel}>{divisionLevel}</option>}
        //   )}
        // </select>
        return <Form>
            <Form.Select  size="sm" className="mt-3 mb-3 pr-3" max-width="200" onChange={this.onChanged}>
                <option value={undefined}>Select...</option>
                {this.props.data?.divisionLevels.map(divisionLevel => {
                   return <option key={'division_level_select_' + divisionLevel} value={divisionLevel}>{divisionLevel}</option>}
               )}
       
              
            </Form.Select>
          </Form>
  }

  links(): [string, string?][] {
    return [
      ["/league/" + this.props.data?.leagueId, this.props.data?.leagueName]
    ]
  }

  externalLink(): JSX.Element | undefined {
    return <ExternalLeagueLink id={this.props.data?.leagueId || 1000} black={false} />
  }
}

export default LeagueTopMenu