import React, { type JSX } from 'react'

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
            img = <img style={{width: '10px', height: '10px'}} src="/external-link.svg" alt={this.path()}/>
        } else {
            img = <img style={{width: '10px', height: '10px'}} src="/external-link-white.svg" alt={this.path()}/>
        }
        return <a href={this.basePath + this.path()} target="_tab">{img}</a>
    }
}

export default ExternalLink
