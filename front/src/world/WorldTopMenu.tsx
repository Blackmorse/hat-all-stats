import React from 'react'
import { Link } from 'react-router-dom'
import TopMenu from '../common/menu/TopMenu'
import WorldData from '../rest/models/leveldata/WorldData'
import { Translation } from 'react-i18next'
import '../i18n'
import i18n from '../i18n'
import { Col, Form, Row } from 'react-bootstrap'

interface Props {
    data?: WorldData,
    callback: (leagueId: number) => void
}

class WorldTopMenu extends TopMenu<WorldData, Props> {
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

    //TODO bootstrap rename
    sectionLinksNew(): Array<{href: string, text: string}> {
        return [
            {href: '/about', text: i18n.t('menu.about')},
            {href: '/worldOverview', text: i18n.t('overview.world_overview')}
        ]
    }

    onChanged = (event: React.FormEvent<HTMLSelectElement>) => {
        this.props.callback(Number(event.currentTarget.value))
      }

    selectBox(): JSX.Element {
        return <Form>
            <Form.Select  size="sm" className="mt-3 mb-3 pr-3 me-md-5" max-width="100" onChange={this.onChanged}>
            <option value={undefined}>Select...</option>
            {this.props.data?.countries.map(countryInfo => {
                return <option value={countryInfo[0]} key={'league_select_' + countryInfo[0]}>
                    {countryInfo[1]}
                </option>
            })}
        </Form.Select>
        </Form> 
    }
}

export default WorldTopMenu