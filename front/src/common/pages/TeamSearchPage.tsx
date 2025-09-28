import React, { type JSX } from 'react'
import TeamSearchResult from '../../rest/models/TeamSearchResult'
import TeamLink from '../links/TeamLink'
import Section, { SectionState } from '../sections/Section'
import SearchTeam from '../search/SearchTeam'
import '../../i18n'
import { Card } from 'react-bootstrap'
import { Translation } from 'react-i18next'
import { PagesEnum } from '../enums/PagesEnum'


interface State {
    results?: Array<TeamSearchResult>
}

class TeamSearchPageBase extends 
        React.Component<{}, State & SectionState> {

    constructor(props: {}) {
        super(props)
        this.state = {collapsed: false}
    }

    componentDidMount() {
        window.scrollTo(0, 0)
    }

    render(): JSX.Element {
        return <Translation>{(t) => <Card className='mt-3 shadow'>
            <Card.Header className='lead'>{t(PagesEnum.TEAM_SEARCH)}</Card.Header>
            <Card.Body>
                <SearchTeam  callback={results => this.setState({results: results})}/>
                    <div className="search_results">
                    {this.state.results?.map(result => {
                        return <React.Fragment  key={'team_search_id_' + result.teamId} > 
                            <TeamLink id={result.teamId} text={result.teamName} forceRefresh={true}/>
                            </React.Fragment>
                    })} 
                    </div>            
            </Card.Body>
        </Card>}
        </Translation>
    }
}

const TeamSearchPage = Section(TeamSearchPageBase, _ => 'menu.team_search')
export default TeamSearchPage
