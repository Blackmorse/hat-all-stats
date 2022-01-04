import React from 'react'
import {MatchRatings} from '../../rest/models/match/TeamMatch'
import { Translation } from 'react-i18next'
import '../../i18n'
import Mappings from '../../common/enums/Mappings'

interface Props {
    matchRatings: MatchRatings
}

class TeamMatchMetaInfo extends React.Component<Props> {
    render() {
        let matchRatings = this.props.matchRatings

        return <Translation>{
            (t, { i18n }) => <table 
            className='small-font border border-1 bg-light shadow-sm border-secondary overflow-visible mt-2 table'
            >
            <tbody>
                <tr>
                    <td className='text-center'>{t('table.hatstats')}</td>
                    <td className='text-end ' style={{whiteSpace: 'nowrap'}}>{matchRatings.ratingMidfield * 3 + matchRatings.ratingLeftAtt + matchRatings.ratingMidAtt + matchRatings.ratingRightAtt + matchRatings.ratingLeftDef + matchRatings.ratingMidDef + matchRatings.ratingRightDef}</td>
                </tr>
                <tr>
                    <td className='text-center'>{t('matches.formation')}</td>
                    <td className='text-end' style={{whiteSpace: 'nowrap'}}>{matchRatings.formation}</td>
                </tr>
                <tr>
                    <td className='text-center'>{t('match.tactic')}</td>
                    <td className='text-end' style={{whiteSpace: 'nowrap'}}>{t(Mappings.tacticType.get(matchRatings.tacticType) || '')}</td>
                </tr>
                <tr>
                    <td className='text-center'>{t('match.tactic_skill')}</td>
                    <td className='text-end' style={{whiteSpace: 'nowrap'}}>{matchRatings.tacticSkill}</td>
                </tr>
            </tbody>
        </table>
    }
    </Translation>
    }
}

export default TeamMatchMetaInfo