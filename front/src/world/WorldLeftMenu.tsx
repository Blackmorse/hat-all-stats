import React from 'react';
import WorldData from '../rest/models/leveldata/WorldData'
import { Translation } from 'react-i18next'
import '../i18n'
import LeagueLink from '../common/links/LeagueLink';
import { Card } from 'react-bootstrap';

interface Props {
    worldData?: WorldData
}

type Country = [number, string]

class WorldLeftMenu extends React.Component<Props, {}> {
    constructor(props: Props) {
        super(props)
        this.state={}
    }

    render() {
        if (this.props.worldData === undefined) {
            return <></>
        }

        let countries = this.props.worldData.countries

        return <Translation>{
            (t, { i18n }) =>
            <Card className="mb-3 shadow">
                <Card.Header className="lead">{t('world.countries')}</Card.Header>
                
                {/* TODO bootstrap add scrollspy-react */}
                <Card.Body>
                    <ul className='d-flex flex-column overflow-auto' style={{maxHeight: '600px'}}>
                         {countries.map(country => {
                             return <LeagueLink key={'world_country_' + country[0]} tableLink={false} id={country[0]} text={country[1]} />
                             
                         })}
                    </ul>

                </Card.Body>
            </Card>
    }
    </Translation>
    }
}

export default WorldLeftMenu
