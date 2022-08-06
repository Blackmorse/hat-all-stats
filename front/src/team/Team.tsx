import React from 'react';
import { useMatch } from 'react-router';
import CountryLevelLayout from '../common/layouts/CountryLevelLayout';
import {getTeamData} from '../rest/Client';
import TeamLevelDataProps from './TeamLevelDataProps';
import pages from './TeamPages';
import TeamTopMenu from './TeamTopMenu';

const Team = () => {
    const pagesMap = pages()
    const teamId = useMatch('/team/:teamId')
    
    return <CountryLevelLayout<TeamLevelDataProps>
            pagesMap={pagesMap}
            documentTitle={levelProps => levelProps.teamName()}
            fetchLevelData={(callback, onError) => getTeamData(Number(teamId?.params.teamId), callback, onError)}
            topMenu={levelProps => <TeamTopMenu levelProps={levelProps}/>}
        />
}

export default Team
