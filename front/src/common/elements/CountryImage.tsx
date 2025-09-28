import React from 'react'

interface Props {
    countryId: number,
    text?: string
}

class CountryImage extends React.Component<Props> {
    render() {
        if(this.props.countryId === 0) {
            return <></>
        }
        const offset = (this.props.countryId === 1000) ? 0 : this.props.countryId * 20
        return <img className=" border me-1 border-secondary" 
            src="/dot.gif" 
            style={{
                background: "url(/flags.gif) -" + offset + "px 0",
                width: '22px',
                height: '14px'
            }}
            alt={this.props.text}
            title={this.props.text}/>
    }
}

export default CountryImage