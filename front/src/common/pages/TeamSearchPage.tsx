import React from 'react'
import TeamSearchResult from '../../rest/models/TeamSearchResult'
import TeamLink from '../links/TeamLink'
import Section, { SectionState } from '../sections/Section'
import SearchTeam from '../search/SearchTeam'


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
        return <><SearchTeam  callback={results => this.setState({results: results})}/>
            <div className="search_results">
            {this.state.results?.map(result => {
                return <React.Fragment  key={'team_search_id_' + result.teamId} > 
                    <TeamLink id={result.teamId} text={result.teamName} forceRefresh={true}/>
                    </React.Fragment>
            })} 
            </div>            
        </>
    }
}

const TeamSearchPage = Section(TeamSearchPageBase, _ => 'menu.team_search')
export default TeamSearchPage