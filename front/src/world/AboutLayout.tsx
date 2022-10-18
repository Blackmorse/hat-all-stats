import React, {useEffect, useState} from 'react';
import Layout from '../common/layouts/Layout';
import '../i18n';
import {getWorldData} from '../rest/clients/LevelDataClient';
import './About.css';
import AboutSection from './AboutSection';
import WorldLeftLoadingMenu from './WorldLeftLoadingMenu';
import WorldLeftMenu from './WorldLeftMenu';
import WorldLevelDataProps from './WorldLevelDataProps';
import WorldTopMenu from './WorldTopMenu';

const AboutLayout = () => {
    const [levelProps, setLevelProps] = useState<WorldLevelDataProps | undefined>(undefined)
    useEffect(() => {
        getWorldData((_loadingEnum, worldData) => setLevelProps(worldData))    
    }, [])

    return <Layout 
        topMenu={<WorldTopMenu levelProps={levelProps} />}
            leftMenu={<>
                <WorldLeftLoadingMenu worldLevelDataProps={levelProps}/>
                <WorldLeftMenu worldLevelDataProps={levelProps}/>
            </>}
        content={<AboutSection />}
        />
}

export default AboutLayout
