import React from 'react'
import './TableLink.css'
import HattidLink, { LinkProps } from './HattidLink';
import ExternalPlayerLink from './ExternalPlayerLink';
import CountryImage from '../elements/CountryImage';
import LeagueLink from './LeagueLink';

interface Props extends LinkProps {
    id: number,
    externalLink?: boolean,
    nationality?: number,
    countriesMap?: Map<number, string>
}

class PlayerLink extends HattidLink<Props> {
    baseString(): string {
        return "/player/" + this.props.id
    }

    postfixAdditionalContent(): JSX.Element {
        if (this.props.externalLink !== undefined && this.props.externalLink) {
            return <>{' '}<ExternalPlayerLink id={this.props.id} /></>
        } else {
            return <></>
        }
    }

    additionalContent(): JSX.Element {
        if (this.props.nationality !== undefined) {
            if (this.props.countriesMap !== undefined) {
                return <LeagueLink forceRefresh={true} id={this.props.nationality} text={<CountryImage countryId={this.props.nationality} text={this.props.countriesMap.get(this.props.nationality)}/>} />
            } else {
                return <CountryImage countryId={this.props.nationality} />
            }
        } else {
            return <></>
        }
    }
}

export default PlayerLink
