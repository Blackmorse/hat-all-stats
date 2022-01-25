import React from 'react';
import { useMatch } from 'react-router';
import CountryLevelLayout from '../common/layouts/CountryLevelLayout';
import {getDivisionLevelData} from '../rest/Client';
import DivisionLevelData from '../rest/models/leveldata/DivisionLevelData';
import DivisionLevelDataProps from './DivisionLevelDataProps';
import pages from './DivisionLevelPages';
import DivisionLevelTopMenu from './DivisionLevelTopMenu';

const DivisionLevel = () => {
    let pageMap = pages()
    let params = useMatch('/league/:league/divisionLevel/:divisionLevel')

    return <CountryLevelLayout<DivisionLevelData, DivisionLevelDataProps>
            pagesMap={pageMap}
            fetchLevelData={(callback, onError) => getDivisionLevelData(Number(params?.params.league), Number(params?.params.divisionLevel), callback, onError)}
            makeModelProps={levelData => new DivisionLevelDataProps(levelData)}
            documentTitle={levelData => levelData.divisionLevelName}
            topMenu={levelData => <DivisionLevelTopMenu data={levelData} />}
        />
}

export default DivisionLevel
