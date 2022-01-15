import React from 'react';
import {useHistory, useParams} from 'react-router';
import CountryLevelLayout from '../common/layouts/CountryLevelLayout';
import {getLeagueData} from '../rest/Client';
import LeagueData from '../rest/models/leveldata/LeagueData';
import LeagueLevelDataProps from './LeagueLevelDataProps';
import pages from './LeaguePages';
import LeagueTopMenu from './LeagueTopMenu';

interface MatchParams {
    leagueId: string;
}

const League = () => {
    const history = useHistory()
    const { leagueId } = useParams<MatchParams>()

    let pagesMap = pages()


    return <CountryLevelLayout<LeagueData, LeagueLevelDataProps>
            pagesMap={pagesMap}
            topMenu={(levelData) => <LeagueTopMenu data={levelData}
                callback={divisionLevel => {history.push('/league/' + leagueId + '/divisionLevel/' + divisionLevel)}}/>}
            fetchLevelData={(callback, onError) => getLeagueData(Number(leagueId), callback, onError)}
            documentTitle={(data) => data.leagueName}
            makeModelProps={data => new LeagueLevelDataProps(data)}
        />
}

export default League
