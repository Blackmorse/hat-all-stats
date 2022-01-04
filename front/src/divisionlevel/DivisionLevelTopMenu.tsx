import React from 'react'
import DivisionLevelData from '../rest/models/leveldata/DivisionLevelData'
import '../common/menu/TopMenu.css'
import TopMenu from '../common/menu/TopMenu';
import { Form } from 'react-bootstrap';


interface Props {
    data?: DivisionLevelData,
    callback: (leagueUnitName: string) => void
}

class DivisionLevelTopMenu extends TopMenu<DivisionLevelData, Props> {

    onChanged = (event: React.FormEvent<HTMLSelectElement>) => {
        this.props.callback(this.props.data?.divisionLevelName + '.' + event.currentTarget.value)
      }

    links(): [string, string?][] {
      return [
        ["/league/" + this.props.data?.leagueId, this.props.data?.leagueName],
        ["/league/" + this.props.data?.leagueId + "/divisionLevel/" + this.props.data?.divisionLevel, this.props.data?.divisionLevelName]
      ]
    }

    externalLink(): JSX.Element | undefined {
      return undefined;
    }
    selectBox(): JSX.Element {
      return <Form>
        <Form.Select  size="sm" className="mt-3 mb-3 pr-3" max-width="200" onChange={this.onChanged}>
                <option value={undefined}>Select...</option>
                {Array.from(Array(this.props.data?.leagueUnitsNumber), (_, i) => i + 1).map(leagueUnit => {
                    return <option key={'division_leve_top_menu_' + leagueUnit} value={leagueUnit}>{leagueUnit}</option>
                })}
            </Form.Select>
          </Form>
    }
}

export default DivisionLevelTopMenu