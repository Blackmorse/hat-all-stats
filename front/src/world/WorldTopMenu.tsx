import React from 'react'
import { Link } from 'react-router-dom'
import TopMenu from '../common/menu/TopMenu'
import WorldData from '../rest/models/leveldata/WorldData'
import { Translation } from 'react-i18next'
import '../i18n'

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

    sectionLinks(): JSX.Element | undefined {
        return <Translation>{
            (t, { i18n }) => <>
            <span className="right_header_section_link">
                <Link className="header_link" to="/about">{t('menu.about')}</Link>
                </span>
            <span className="right_header_section_link">
                <Link className="header_link" to="/worldOverview">{t('overview.world_overview')}</Link>
            </span>
        </>
        }</Translation>
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