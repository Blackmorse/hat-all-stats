import React from 'react'
import StatisticsSection from '../sections/StatisticsSection'
import TeamSearchResult from '../../rest/models/TeamSearchResult'
import { Translation } from 'react-i18next'
import '../../i18n'
import { searchTeam } from '../../rest/Client'
import i18n from '../../i18n'
import TeamLink from '../links/TeamLink'
import './TeamSearchPage.css'
import { LoadingEnum } from '../enums/LoadingEnum'

interface State {
    loadingState: LoadingEnum,
    results?: Array<TeamSearchResult>,
    currentSearch: string
}

class TeamSearchPage extends StatisticsSection<{}, State> {
    constructor(props: {}) {
        super(props, 'menu.team_search')
        this.state = {
            loadingState: LoadingEnum.OK,
            currentSearch: ""
        }

        this.changeHandler=this.changeHandler.bind(this)
        this.clickHandler=this.clickHandler.bind(this)
    }

    componentDidMount() {
        window.scrollTo(0, 0)
    }

    updateCurrent(): void {
        this.setState({
            results: this.state.results,
            loadingState: LoadingEnum.LOADING,
            currentSearch: this.state.currentSearch
        })

        searchTeam(this.state.currentSearch,
            (loadingStatus, results) => this.setState({
                results: (results) ? results : this.state.results,
                loadingState: loadingStatus,
                currentSearch: this.state.currentSearch
            }))
    }

    changeHandler = (event: React.ChangeEvent<HTMLInputElement>) => {
        this.setState({
            currentSearch: event.currentTarget.value,
            loadingState: LoadingEnum.OK,
            results: this.state.results
        })
      }

    clickHandler() {
        this.updateCurrent()
    }

    renderSection(): JSX.Element {
        let a = i18n.t('menu.search')
        return <Translation>{
            (t, { i18n }) => <div className="search_section">
            <div className="search_form">
                <input type="text" value={this.state.currentSearch} onChange={this.changeHandler}/>
                <input type="submit" value={a} onClick={this.clickHandler}/>
            </div>

            {this.state.results?.map(result => {
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

export default TeamSearchPage