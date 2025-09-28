import { useMatch } from 'react-router';
import CountryLevelLayout from '../common/layouts/CountryLevelLayout';
import {getDivisionLevelData} from '../rest/clients/LevelDataClient';
import DivisionLevelDataProps from './DivisionLevelDataProps';
import pages from './DivisionLevelPages';
import DivisionLevelTopMenu from './DivisionLevelTopMenu';

const DivisionLevel = () => {
    const pageMap = pages()
    const params = useMatch('/league/:league/divisionLevel/:divisionLevel')

    return <CountryLevelLayout<DivisionLevelDataProps>
            pagesMap={pageMap}
            fetchLevelData={callback => getDivisionLevelData(Number(params?.params.league), Number(params?.params.divisionLevel), callback)}
            documentTitle={levelProps => levelProps.divisionLevelName()}
            topMenu={levelProps => <DivisionLevelTopMenu levelProps={levelProps} />}
        />
}

export default DivisionLevel
