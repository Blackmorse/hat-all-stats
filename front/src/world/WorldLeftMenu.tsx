import React from 'react';
import WorldData from '../rest/models/leveldata/WorldData'
import { Translation } from 'react-i18next'
import '../i18n'
import LeagueLink from '../common/links/LeagueLink';
import './WorldLeftMenu.css'
import '../common/menu/LeftMenu.css'
import { Card, Col, Container, Row } from 'react-bootstrap';

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
        let letterToCountriesMap = new Map<string, Array<Country>>()
        let letters: Array<string> = []

        for (let country of countries) {
            let letter = country[1].charAt(0)
            letters.push(letter)
            if (letterToCountriesMap.has(letter)) {
                letterToCountriesMap.get(letter)?.push(country)
            } else {
                letterToCountriesMap.set(letter, [ country ])
            }
        }

        let lettersSet = new Set(letters)
        let props =  {tabIndex: 0} 

        return <Translation>{
            (t, { i18n }) =>
            <Card className="mt-3 shadow">
                <Card.Header className="lead">{t('world.countries')}</Card.Header>
                
                {/* TODO bootstrap add scrollspy-react */}
                <Card.Body>
                    <Row>
                        <Col  lg={1} className="mb-0 me-1 mt-2 d-none d-lg-block" >
                            <div id="countries_group_list" className="list-group mb-0 me-1" >
                                {Array.from(lettersSet).map(letter => 
                                    <a className="list-group-item list-group-item-action pb-0 pe-3 pt-0 pb-0" href={'#country_letter_' + letter}>
                                        {letter}
                                    </a>)}
                            </div>
                        </Col>
                        <Col>
                        <Container>
                            <div data-bs-spy="scroll" data-bs-target="#countries_group_list" data-bs-offset="0" className="scrollspy-example scrolled" {...props} >
                            {Array.from(lettersSet).map(letter => {
                                return <><h5 id={'country_letter_' + letter}></h5>
                                        {letterToCountriesMap.get(letter)?.map(country => {
                                                return <LeagueLink key={'world_country_' + country[0]} tableLink={false} id={country[0]} text={country[1]} />
                                        })}
                                    </> 
                            })}
                            </div>
                        </Container>
                        </Col>
                    </Row>
                </Card.Body>
            </Card>
    }
    </Translation>
    }
}

export default WorldLeftMenu
