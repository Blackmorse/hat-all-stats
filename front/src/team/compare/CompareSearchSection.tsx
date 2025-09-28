import React, { type JSX } from 'react'
import { PagesEnum } from '../../common/enums/PagesEnum';
import TeamLink from '../../common/links/TeamLink';
import SearchTeam from '../../common/search/SearchTeam';
import Section, { SectionState } from '../../common/sections/Section';
import TeamSearchResult from "../../rest/models/TeamSearchResult";
import TeamLevelDataProps from '../TeamLevelDataProps';

interface State {
    results?: Array<TeamSearchResult>
}

interface Props {
    levelDataProps: TeamLevelDataProps
}

class CompareSearch extends React.Component<Props, State & SectionState>{
    constructor(props: Props) {
        super(props)
        this.state = {collapsed: false}
    }

    render(): JSX.Element {
        return <>
            <SearchTeam  callback={results => this.setState({results: results})}/>
            <div className="search_results">
            {this.state.results?.map(team => {
                return <TeamLink text={team.teamName} 
                    key={`team-${team.teamId}`}
                    id={this.props.levelDataProps.teamId()} 
                    page={PagesEnum.TEAM_COMPARSION}
                    queryParams={{
                        teamId: team.teamId
                    }}
                forceRefresh={true} />
            })}</div>
        </>
    }
}

const CompareSearchSection = Section(CompareSearch, _ => 'menu.comparsion_of_teams')

export default CompareSearchSection
