import React from 'react';
import { useMatch } from 'react-router';
import CountryLevelLayout from '../common/layouts/CountryLevelLayout';
import {getTeamData} from '../rest/Client';
import TeamData from '../rest/models/leveldata/TeamData';
import TeamLevelDataProps from './TeamLevelDataProps';
import pages from './TeamPages';
import TeamTopMenu from './TeamTopMenu';

const Team = () => {
    const pagesMap = pages()
    const teamId = useMatch('/team/:teamId')
    
    return <CountryLevelLayout<TeamData, TeamLevelDataProps>
            pagesMap={pagesMap}
            documentTitle={levelData => levelData.teamName}
            makeModelProps={levelData => new TeamLevelDataProps(levelData)}
            fetchLevelData={(callback, onError) => getTeamData(Number(teamId?.params.teamId), callback, onError)}
            topMenu={levelData => <TeamTopMenu data={levelData}/>}
        />
}

export default Team
