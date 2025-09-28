import React, { type JSX } from 'react'
import '../../i18n'
import { Translation } from 'react-i18next'
import moment from 'moment'
import i18n from '../../i18n'
import { Card, Container } from 'react-bootstrap'
import CountryLevelDataProps from '../CountryLevelDataProps'

interface Props {
    levelDataProps: CountryLevelDataProps
}

class CurrentCountryInfoMenu extends React.Component<Props> {
    render() {
        let dateString: JSX.Element = <></>
        if(this.props.levelDataProps.loadingInfo().loadingInfo === "scheduled") {
            dateString = <>{i18n.t('league.next_round_scheduled')} <br/> {moment(this.props.levelDataProps.levelData.loadingInfo.date).format('DD.MM HH:mm:ss')} HT</>
        } else if(this.props.levelDataProps.levelData.loadingInfo.loadingInfo === "loading") {
            dateString = <>{i18n.t('league.next_round_loading')}</>
        }
        return <Translation>{
              t =>  
                <Card className="mb-3 shadow">
                    <Card.Header className="lead">{this.props.levelDataProps.levelData.leagueName}</Card.Header>
                    <Card.Body>
                        <Container d-flex='true' className='flex-column'>
                            <div>{t('filter.season')}: {this.props.levelDataProps.offsettedSeason()} </div>
                            <div className='mb-2'>{t('filter.round')} {this.props.levelDataProps.currentRound()}</div>
                            <div>{dateString}</div>
                        </Container>                        
                    </Card.Body>
                </Card>
        }
        </Translation>
    }
}

export default CurrentCountryInfoMenu
