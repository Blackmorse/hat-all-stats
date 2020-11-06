import React from 'react'
import DivisionLevelData from '../rest/models/leveldata/DivisionLevelData'
import '../common/menu/TopMenu.css'
import TopMenu from '../common/menu/TopMenu';


interface Props {
    divisionLevelData?: DivisionLevelData,
    callback: (leagueUnitName: string) => void
}

class DivisionLevelTopMenu extends TopMenu<Props> {
    
    onChanged = (event: React.FormEvent<HTMLSelectElement>) => {
        this.props.callback(this.props.divisionLevelData?.divisionLevelName + '.' + event.currentTarget.value)
      }

    links(): [string, string?][] {
      return [
        ["/league/" + this.props.divisionLevelData?.leagueId, this.props.divisionLevelData?.leagueName],
        ["/league/" + this.props.divisionLevelData?.leagueId + "/divisionLevel/" + this.props.divisionLevelData?.divisionLevel, this.props.divisionLevelData?.divisionLevelName]
      ]
    }
    selectBox(): JSX.Element {
      return <select className="href_select" onChange={this.onChanged}>
                <option value={undefined}>Select...</option>
                {Array.from(Array(this.props.divisionLevelData?.leagueUnitsNumber), (_, i) => i + 1).map(leagueUnit => {
                    return <option value={leagueUnit}>{leagueUnit}</option>
                })}
            </select>
    }
}

export default DivisionLevelTopMenu