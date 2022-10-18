import React from 'react';
import { useMatch} from 'react-router';
import CountryLevelLayout from '../common/layouts/CountryLevelLayout';
import {getLeagueUnitData} from '../rest/clients/LevelDataClient';
import LeagueUnitLevelDataProps from './LeagueUnitLevelDataProps';
import pages from './LeagueUnitPages';
import LeagueUnitTopMenu from './LeagueUnitTopMenu';

const LeagueUnit = () => {
    let pagesMap = pages()
    let params = useMatch('leagueUnit/:leagueUnit')

    return <CountryLevelLayout<LeagueUnitLevelDataProps>
            pagesMap={pagesMap}
            fetchLevelData={callback => getLeagueUnitData(Number(params?.params.leagueUnit), callback)}
            documentTitle={levelProps => levelProps.leagueUnitName()}
            topMenu={levelProps => <LeagueUnitTopMenu levelProps={levelProps} />}
        />
}

export default LeagueUnit
