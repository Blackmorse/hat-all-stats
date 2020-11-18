import React from 'react'
import {MatchRatings} from '../../rest/models/match/TeamMatch'
import { Translation } from 'react-i18next'
import '../../i18n'
import Mappings from '../../common/enums/Mappings'
import './TeamMatchMetaInfo.css'

interface Props {
    matchRatings: MatchRatings
}

class TeamMatchMetaInfo extends React.Component<Props> {
    render() {
        let matchRatings = this.props.matchRatings

        return <Translation>{
            (t, { i18n }) => <table className="side_match_info_table">
        <tbody>
            <tr>
                <td>{t('table.hatstats')}</td>
                <td>{matchRatings.ratingMidfield * 3 + matchRatings.ratingLeftAtt + matchRatings.ratingMidAtt + matchRatings.ratingRightAtt + matchRatings.ratingLeftDef + matchRatings.ratingMidDef + matchRatings.ratingRightDef}</td>
            </tr>
            <tr>
                <td>{t('matches.formation')}</td>
                <td>{matchRatings.formation}</td>
            </tr>
            <tr>
                <td>{t('match.tactic')}</td>
                <td>{t(Mappings.tacticType.get(matchRatings.tacticType) || '')}</td>
            </tr>
            <tr>
                <td>{t('match.tactic_skill')}</td>
                <td>{matchRatings.tacticSkill}</td>
            </tr>
        </tbody>
    </table>
    }
    </Translation>
    }
}

export default TeamMatchMetaInfo