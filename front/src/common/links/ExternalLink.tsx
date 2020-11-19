import React from 'react'
import './ExternalLink.css'

interface Props {
    id: number,
    black?: boolean
}

abstract class ExternalLink extends React.Component<Props> {
    basePath = 'https://www.hattrick.org/goto.ashx?path='
    
    abstract path(): string

    render() {
        let img: JSX.Element
        if (this.props.black === undefined || this.props.black) {
            img = <img className="external_table_img" src="/external-link.svg" alt="external_link"/>
        } else {
            img = <img className="external_table_img" src="/external-link-white.svg" alt="external_link"/>
        }
        return <a href={this.basePath + this.path()} target="_tab">{img}</a>
    }
}

export default ExternalLink