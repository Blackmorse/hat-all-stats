import HattidLink, { LinkProps } from './HattidLink';
import React from 'react'
import LeagueLink from './LeagueLink'
import CountryImage from '../elements/CountryImage';

interface Props extends LinkProps {
    id: number,
    flagCountryNumber?: number
}

class TeamLink extends HattidLink<Props> {
    baseString(): string {
        return "/team/" + this.props.id
    }

    additionalContent(): JSX.Element {
        let flagLink: JSX.Element = <></>
        if (this.props.flagCountryNumber !== undefined) {
            let flagNumber = (this.props.flagCountryNumber) as number
            flagLink = <LeagueLink id={flagNumber} forceRefresh={true}
             text={<CountryImage countryId={flagNumber} />}/>
        }
        return flagLink
    }
}

export default TeamLink