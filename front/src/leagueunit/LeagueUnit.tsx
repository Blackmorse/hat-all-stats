import React from 'react';
import { useMatch, useNavigate} from 'react-router';
import CountryLevelLayout from '../common/layouts/CountryLevelLayout';
import {getLeagueUnitData} from '../rest/Client';
import LeagueUnitData from '../rest/models/leveldata/LeagueUnitData';
import LeagueUnitLevelDataProps from './LeagueUnitLevelDataProps';
import pages from './LeagueUnitPages';
import LeagueUnitTopMenu from './LeagueUnitTopMenu';

const LeagueUnit = () => {
    let pagesMap = pages()
    let params = useMatch('leagueUnit/:leagueUnit')
    let navigate = useNavigate()

    function teamIdSelected(teamId: number) {
        navigate('/team/' + teamId)
    }

    return <CountryLevelLayout<LeagueUnitData, LeagueUnitLevelDataProps>
            pagesMap={pagesMap}
            fetchLevelData={(callback, onError) => getLeagueUnitData(Number(params?.params.leagueUnit), callback, onError)}
            makeModelProps={levelData => new LeagueUnitLevelDataProps(levelData)}
            documentTitle={data => data.leagueUnitName}
            topMenu={levelData => <LeagueUnitTopMenu data={levelData}
                  callback={teamIdSelected} />}
        />
}

export default LeagueUnit
