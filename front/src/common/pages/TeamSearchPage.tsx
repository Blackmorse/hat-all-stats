import React from 'react'
import TeamSearchResult from '../../rest/models/TeamSearchResult'
import { Translation } from 'react-i18next'
import '../../i18n'
import { searchTeam, searchTeamById } from '../../rest/Client'
import i18n from '../../i18n'
import TeamLink from '../links/TeamLink'
import './TeamSearchPage.css'
import { LoadingEnum } from '../enums/LoadingEnum'
import ExecutableComponent, { LoadableState } from '../sections/ExecutableComponent'
import Section, { SectionState } from '../sections/Section'

enum TeamSearchType {
    ID, NAME
}

interface State {
    results?: Array<TeamSearchResult>
}

type DataRequest = {search: string, searchType: TeamSearchType}

class TeamSearchPageBase extends 
        ExecutableComponent<{}, State, Array<TeamSearchResult>, DataRequest, LoadableState<State, DataRequest> & SectionState> {
    
    constructor(props: {}) {
        super(props)
        this.state = {
            loadingState: LoadingEnum.OK,
            dataRequest: {
                searchType: TeamSearchType.NAME,
                search: ""
            },
            state: {},
            collapsed: false
        }

        this.clickHandler=this.clickHandler.bind(this)
    }

    componentDidMount() {
        window.scrollTo(0, 0)
    }

    executeDataRequest(dataRequest: DataRequest, callback: (loadingState: LoadingEnum, result?: Array<TeamSearchResult>) => void): void {

        if (dataRequest.searchType === TeamSearchType.NAME) {
            let name = dataRequest.search
            if (name && name.trim().length > 0) {
                searchTeam(this.state.dataRequest.search, callback)
                return
            } 
            
        } else if (dataRequest.searchType === TeamSearchType.ID) {
            if (dataRequest.search.match(/^[0-9]+$/) != null) {
                searchTeamById(Number(dataRequest.search), callback)
                return
            }            
        }
        callback(LoadingEnum.OK, [])   
    }

    stateFromResult(result?: Array<TeamSearchResult>): State {
        return {
            results: (result) ? result : this.state.state.results
        }
    }

    textChangeHandler = (event: React.ChangeEvent<HTMLInputElement>) => {
        this.setState({
            dataRequest: {
                search: event.currentTarget.value,
                searchType: this.state.dataRequest.searchType
            }
        })
      }

    clickHandler() {
        this.update()
    }

    searchTypeChangeHandler = (event: React.FormEvent<HTMLSelectElement>) => {
        this.setState({
            dataRequest: {
                search: this.state.dataRequest.search,
                searchType: event.currentTarget.value === "1" ? TeamSearchType.NAME : TeamSearchType.ID
            }
        })
    }

    renderSection(): JSX.Element {
        let a = i18n.t('menu.search')
        return <Translation>{
            (t, { i18n }) => <div className="search_section">
            <select defaultValue={this.state.dataRequest.searchType} onChange={this.searchTypeChangeHandler}>
                <option value={TeamSearchType.NAME.toString()}>{t('table.team')}</option>
                <option value={TeamSearchType.ID.toString()}>ID</option>
            </select>

            <div className="search_form">
                <input type="text" value={this.state.dataRequest.search} onChange={this.textChangeHandler}/>
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