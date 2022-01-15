import React from 'react';
import {useHistory, useParams} from 'react-router';
import CountryLevelLayout from '../common/layouts/CountryLevelLayout';
import {getDivisionLevelData, getLeagueUnitIdByName} from '../rest/Client';
import DivisionLevelData from '../rest/models/leveldata/DivisionLevelData';
import DivisionLevelDataProps from './DivisionLevelDataProps';
import pages from './DivisionLevelPages';
import DivisionLevelTopMenu from './DivisionLevelTopMenu';

interface MatchParams {
    leagueId: string,
    divisionLevel: string
}

const DivisionLevel = () => {
    let pageMap = pages()
    let { leagueId, divisionLevel } = useParams<MatchParams>()
    let history = useHistory()

    function leagueUnitSelected(leagueUnitName: string) {
        getLeagueUnitIdByName(Number(leagueId), leagueUnitName, id => {
            history.push('/leagueUnit/' + id)
        })
    }

    return <CountryLevelLayout<DivisionLevelData, DivisionLevelDataProps>
            pagesMap={pageMap}
            fetchLevelData={(callback, onError) => getDivisionLevelData(Number(leagueId), Number(divisionLevel), callback, onError)}
            makeModelProps={levelData => new DivisionLevelDataProps(levelData)}
            documentTitle={levelData => levelData.divisionLevelName}
            topMenu={levelData => <DivisionLevelTopMenu data={levelData} 
                callback={leagueUnitSelected}/>}
        />
}

export default DivisionLevel
