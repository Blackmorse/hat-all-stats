import React from 'react';
import { StatsTypeEnum, StatsType } from '../../rest/StatisticsParameters'
import './Selector.css'
import '../../i18n'
import { Translation } from 'react-i18next'

interface StatsTypeSelectorProps {
    currentRound: number
    rounds: Array<number>
    statsTypes: Array<StatsTypeEnum>
    selectedStatType: StatsType
    onChanged: (newStatsType: StatsType) => void
}

class StatsTypeSelector extends React.Component<StatsTypeSelectorProps> {
    
    onChanged = (event: React.FormEvent<HTMLSelectElement>) => {
        if (event.currentTarget.value === "avg") {
            this.props.onChanged({statType: StatsTypeEnum.AVG})
        } else if (event.currentTarget.value === "max") {
            this.props.onChanged({statType: StatsTypeEnum.MAX})
        } else if (event.currentTarget.value === "accumulate") {
            this.props.onChanged({statType: StatsTypeEnum.ACCUMULATE})
        } else if (event.currentTarget.value.startsWith("statRound")) {
            const round = Number(event.currentTarget.value.split(":")[1])
            this.props.onChanged({statType: StatsTypeEnum.ROUND, roundNumber: round})
        }
    }

    render() {        
        const avgOption = (this.props.statsTypes.includes(StatsTypeEnum.AVG)) ?
         <option 
            value={StatsTypeEnum.AVG.toString()}>avg</option> : <></>

        const maxOption = (this.props.statsTypes.includes(StatsTypeEnum.MAX)) ?
         <option
            value={StatsTypeEnum.MAX.toString()}>max</option> : <></>

        const accumulateOption = (this.props.statsTypes.includes(StatsTypeEnum.ACCUMULATE)) ?
            <option 
               value={StatsTypeEnum.ACCUMULATE.toString()}>all</option> : <></>

        const roundOptions = (this.props.statsTypes.includes(StatsTypeEnum.ROUND)) ?
         <>
                {this.props.rounds.map(round => {
                    return <option key={"select_round_" + round}
                            value={StatsTypeEnum.ROUND.toString() + ":" + round}>
                                {round}
                            </option>
                })
                }     
        </> 
         : <></>


        return <Translation>
            { (t, { i18n }) =>
            <div className="selector_div">
                <span className="selector_div_entry">{t('filter.round')}</span>
                <select className="selector_div_entry" onChange={this.onChanged} 
                    defaultValue={this.props.selectedStatType.statType.toString() + (this.props.selectedStatType.roundNumber) ? ":" + this.props.selectedStatType.roundNumber : ""}>
                    {avgOption}
                    {maxOption}
                    {accumulateOption}
                    {roundOptions}
                </select>
            </div>
            }
        </Translation>
    }
}

export default StatsTypeSelector