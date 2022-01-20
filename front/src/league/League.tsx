import React from 'react';
import { useMatch, useNavigate} from 'react-router';
import CountryLevelLayout from '../common/layouts/CountryLevelLayout';
import {getLeagueData} from '../rest/Client';
import LeagueData from '../rest/models/leveldata/LeagueData';
import LeagueLevelDataProps from './LeagueLevelDataProps';
import pages from './LeaguePages';
import LeagueTopMenu from './LeagueTopMenu';

const League = () => {
    const navigate = useNavigate()
    const params = useMatch('league/:league')

    let pagesMap = pages()


    return <CountryLevelLayout<LeagueData, LeagueLevelDataProps>
            pagesMap={pagesMap}
            topMenu={(levelData) => <LeagueTopMenu data={levelData}
                callback={divisionLevel => {navigate('/league/' + params?.params.league + '/divisionLevel/' + divisionLevel)}}/>}
            fetchLevelData={(callback, onError) => getLeagueData(Number(params?.params.league), callback, onError)}
            documentTitle={(data) => data.leagueName}
            makeModelProps={data => new LeagueLevelDataProps(data)}
        />
}

export default League
