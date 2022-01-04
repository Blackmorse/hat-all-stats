import React from 'react'
import TopMenu from '../common/menu/TopMenu'
import WorldData from '../rest/models/leveldata/WorldData'
import '../i18n'
import i18n from '../i18n'
import { Form } from 'react-bootstrap'

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

    sectionLinks(): Array<{href: string, text: string}> {
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
            <Form.Select  size="sm" className="my-1 pr-3 me-md-5" max-width="100" onChange={this.onChanged}>
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