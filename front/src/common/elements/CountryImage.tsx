import React from 'react'
import './CountryImage.css'

interface Props {
    countryId: number,
    text?: string
}

class CountryImage extends React.Component<Props> {
    render() {
        if(this.props.countryId === 0) {
            return <></>
        }
        let offset = (this.props.countryId === 1000) ? 0 : this.props.countryId * 20
        return <img className="country_image"
            src="/dot.gif" 
            style={{background: "url(/flags.gif) -" + offset + "px 0"}}
            alt={this.props.text}
            title={this.props.text}/>
    }
}

export default CountryImage