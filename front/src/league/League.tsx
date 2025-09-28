import { useMatch} from 'react-router';
import CountryLevelLayout from '../common/layouts/CountryLevelLayout';
import {getLeagueData} from '../rest/clients/LevelDataClient';
import LeagueLevelDataProps from './LeagueLevelDataProps';
import pages from './LeaguePages';
import LeagueTopMenu from './LeagueTopMenu';

const League = () => {
    const params = useMatch('league/:league')

    const pagesMap = pages()

    return <CountryLevelLayout<LeagueLevelDataProps>
            pagesMap={pagesMap}
            topMenu={(levelProps) => <LeagueTopMenu levelProps={levelProps} />}
            fetchLevelData={callback => getLeagueData(Number(params?.params.league), callback)}
            documentTitle={(levelProps) => levelProps.leagueName()}
        />
}

export default League
