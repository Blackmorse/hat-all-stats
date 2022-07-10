import React from 'react';
import { useMatch } from 'react-router';
import CountryLevelLayout from '../common/layouts/CountryLevelLayout';
import {getDivisionLevelData} from '../rest/Client';
import DivisionLevelDataProps from './DivisionLevelDataProps';
import pages from './DivisionLevelPages';
import DivisionLevelTopMenu from './DivisionLevelTopMenu';

const DivisionLevel = () => {
    let pageMap = pages()
    let params = useMatch('/league/:league/divisionLevel/:divisionLevel')

    return <CountryLevelLayout<DivisionLevelDataProps>
            pagesMap={pageMap}
            fetchLevelData={(callback, onError) => getDivisionLevelData(Number(params?.params.league), Number(params?.params.divisionLevel), callback, onError)}
            documentTitle={levelProps => levelProps.divisionLevelName()}
            topMenu={levelProps => <DivisionLevelTopMenu levelProps={levelProps} />}
        />
}

export default DivisionLevel
