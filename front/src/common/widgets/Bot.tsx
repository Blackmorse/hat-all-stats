import React from 'react'
import '../../i18n'
import { Translation } from 'react-i18next'
import './Bot.css'

class Bot extends React.Component<{}, {}> {
    render() {
        return <Translation>
        {(t, { i18n}) => <div className="info_div">
        <div className="info_text">{t('bot.description')}</div>
        <div className="info_image">
            <img src="/football-robot.png" alt="This is bot"/>
        </div>
    </div>
    }
    </Translation>
    }
}

export default Bot