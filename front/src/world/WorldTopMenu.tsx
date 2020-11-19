import React from 'react'
import TopMenu from '../common/menu/TopMenu'
import WorldData from '../rest/models/leveldata/WorldData'

interface Props {
    worldData?: WorldData,
    callback: (leagueId: number) => void
}

class WorldTopMenu extends TopMenu<Props> {
    links(): [string, string?][] {
        return []
    }

    externalLink(): JSX.Element | undefined {
        return undefined
    }

    onChanged = (event: React.FormEvent<HTMLSelectElement>) => {
        this.props.callback(Number(event.currentTarget.value))
      }

    selectBox(): JSX.Element {
        return <select className="href_select" onChange={this.onChanged}>
            <option value={undefined}>Select...</option>
            {this.props.worldData?.countries.map(countryInfo => {
                return <option value={countryInfo[0]} key={'league_select_' + countryInfo[0]}>
                    {countryInfo[1]}
                </option>
            })}
        </select>
    }
}

export default WorldTopMenu