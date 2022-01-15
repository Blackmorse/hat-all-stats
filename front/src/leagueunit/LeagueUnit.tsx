import React from 'react';
import {useHistory, useParams} from 'react-router';
import CountryLevelLayout from '../common/layouts/CountryLevelLayout';
import {getLeagueUnitData} from '../rest/Client';
import LeagueUnitData from '../rest/models/leveldata/LeagueUnitData';
import LeagueUnitLevelDataProps from './LeagueUnitLevelDataProps';
import pages from './LeagueUnitPages';
import LeagueUnitTopMenu from './LeagueUnitTopMenu';

interface MatchParams {
    leagueUnitId: string
}

const LeagueUnit = () => {
    let pagesMap = pages()
    let { leagueUnitId } = useParams<MatchParams>()
    let history = useHistory()

    function teamIdSelected(teamId: number) {
        history.push('/team/' + teamId)
    }

    return <CountryLevelLayout<LeagueUnitData, LeagueUnitLevelDataProps>
            pagesMap={pagesMap}
            fetchLevelData={(callback, onError) => getLeagueUnitData(Number(leagueUnitId), callback, onError)}
            makeModelProps={levelData => new LeagueUnitLevelDataProps(levelData)}
            documentTitle={data => data.leagueUnitName}
            topMenu={levelData => <LeagueUnitTopMenu data={levelData}
                  callback={teamIdSelected} />}
        />
}

export default LeagueUnit
