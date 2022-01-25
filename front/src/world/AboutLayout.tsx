import React, {useEffect, useState} from 'react';
import Layout from '../common/layouts/Layout';
import '../i18n';
import {getWorldData} from '../rest/Client';
import WorldData from '../rest/models/leveldata/WorldData';
import './About.css';
import AboutSection from './AboutSection';
import WorldLeftLoadingMenu from './WorldLeftLoadingMenu';
import WorldLeftMenu from './WorldLeftMenu';
import WorldTopMenu from './WorldTopMenu';

const AboutLayout = () => {
    const [levelData, setLevelData] = useState(undefined as WorldData | undefined)
    useEffect(() => {
        getWorldData(worldData => setLevelData(worldData), () => {})    
    }, [])

    return <Layout 
        topMenu={<WorldTopMenu data={levelData} />}
            leftMenu={<>
                <WorldLeftLoadingMenu worldData={levelData}/>
                <WorldLeftMenu worldData={levelData}/>
            </>}
        content={<AboutSection />}
        />
}

export default AboutLayout
