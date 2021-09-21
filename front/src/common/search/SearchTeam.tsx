import React from 'react'
import ExecutableComponent, { LoadableState } from '../sections/ExecutableComponent'
import TeamSearchResult from '../../rest/models/TeamSearchResult'
import { SectionState } from '../sections/Section'
import { LoadingEnum } from '../enums/LoadingEnum'
import { searchTeam, searchTeamById } from '../../rest/Client'
import { Translation } from 'react-i18next'
import '../../i18n'
import i18n from '../../i18n'
import './SearchTeam.css'

enum TeamSearchType {
    ID, NAME
}

interface Props {
    callback: (results?: Array<TeamSearchResult>) => void
}

type DataRequest = {search: string, searchType: TeamSearchType}

class SearchTeam extends 
        ExecutableComponent<Props, {}, Array<TeamSearchResult>, DataRequest, LoadableState<{}, DataRequest> & SectionState> {
    
    constructor(props: Props) {
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

    executeDataRequest(dataRequest: DataRequest, callback: (loadingState: LoadingEnum, result?: TeamSearchResult[]) => void): void {
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
    
    stateFromResult(result?: TeamSearchResult[]): {} {
        this.props.callback(result)
        return {}
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
        </div>
        }
        </Translation>
    }   
}

export default SearchTeam