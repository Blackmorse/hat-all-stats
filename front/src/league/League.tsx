import React from 'react';
import { useMatch} from 'react-router';
import CountryLevelLayout from '../common/layouts/CountryLevelLayout';
import {getLeagueData} from '../rest/Client';
import LeagueLevelDataProps from './LeagueLevelDataProps';
import pages from './LeaguePages';
import LeagueTopMenu from './LeagueTopMenu';

const League = () => {
    const params = useMatch('league/:league')

    let pagesMap = pages()

    return <CountryLevelLayout<LeagueLevelDataProps>
            pagesMap={pagesMap}
            topMenu={(levelProps) => <LeagueTopMenu levelProps={levelProps} />}
            fetchLevelData={(callback, onError) => getLeagueData(Number(params?.params.league), callback, onError)}
            documentTitle={(levelProps) => levelProps.leagueName()}
        />
}

export default League
