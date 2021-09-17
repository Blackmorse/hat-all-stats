import React from 'react'
import TeamSearchResult from '../../rest/models/TeamSearchResult'
import { Translation } from 'react-i18next'
import '../../i18n'
import { searchTeam } from '../../rest/Client'
import i18n from '../../i18n'
import TeamLink from '../links/TeamLink'
import './TeamSearchPage.css'
import { LoadingEnum } from '../enums/LoadingEnum'
import ExecutableComponent, { LoadableState } from '../sections/ExecutableComponent'
import Section, { SectionState } from '../sections/Section'

interface State {
    results?: Array<TeamSearchResult>,
}

class TeamSearchPageBase extends 
        ExecutableComponent<{}, State, Array<TeamSearchResult>, string, LoadableState<State, string> & SectionState> {
    
    constructor(props: {}) {
        super(props)
        this.state = {
            loadingState: LoadingEnum.OK,
            dataRequest: "",
            state: {},
            collapsed: false
        }

        this.changeHandler=this.changeHandler.bind(this)
        this.clickHandler=this.clickHandler.bind(this)
    }

    componentDidMount() {
        window.scrollTo(0, 0)
    }

    executeDataRequest(dataRequest: string, callback: (loadingState: LoadingEnum, result?: Array<TeamSearchResult>) => void): void {
        let name = this.state.dataRequest
        if (name && name.trim().length > 0) {
            searchTeam(this.state.dataRequest, callback)
        } else {
            callback(LoadingEnum.OK, [])
        }
    }

    stateFromResult(result?: Array<TeamSearchResult>): State {
        return {
            results: (result) ? result : this.state.state.results
        }
    }

    changeHandler = (event: React.ChangeEvent<HTMLInputElement>) => {
        this.setState({
            dataRequest: event.currentTarget.value
        })
      }

    clickHandler() {
        this.update()
    }

    renderSection(): JSX.Element {
        let a = i18n.t('menu.search')
        return <Translation>{
            (t, { i18n }) => <div className="search_section">
            <div className="search_form">
                <input type="text" value={this.state.dataRequest} onChange={this.changeHandler}/>
                <input type="submit" value={a} onClick={this.clickHandler}/>
            </div>

            {this.state.state.results?.map(result => {
                return <React.Fragment  key={'team_search_id_' + result.teamId}> 
                    <TeamLink id={result.teamId} text={result.teamName} forceRefresh={true}/>
                    <br />
                    </React.Fragment>
            })} 
            
            
        </div>
        }
        </Translation>
    }
}

const TeamSearchPage = Section(TeamSearchPageBase, _ => 'menu.team_search')
export default TeamSearchPage