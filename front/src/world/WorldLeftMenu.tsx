import React from 'react';
import { Translation } from 'react-i18next'
import '../i18n'
import LeagueLink from '../common/links/LeagueLink';
import { Card } from 'react-bootstrap';
import WorldLevelDataProps from './WorldLevelDataProps';

interface Props {
    worldLevelDataProps?: WorldLevelDataProps
}

class WorldLeftMenu extends React.Component<Props, {}> {
    constructor(props: Props) {
        super(props)
        this.state={}
    }

    render() {
        if (this.props.worldLevelDataProps === undefined) {
            return <></>
        }

        let countries = this.props.worldLevelDataProps.countries()

        return <Translation>{
            t =>
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
