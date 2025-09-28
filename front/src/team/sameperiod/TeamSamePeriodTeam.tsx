import React from 'react'
import CreatedSameTimeTeamExtended, {CreatedSameTimeTeamRequest} from '../../rest/models/team/CreatedSameTimeTeamExtended';
import { useTranslation } from 'react-i18next'
import { LevelDataPropsWrapper } from '../../common/LevelDataProps'
import TeamLevelDataProps from '../TeamLevelDataProps'
import { getCreatedSameTimeTeams } from '../../rest/Client'
import ExecutableComponent ,{ StateAndRequest } from '../../common/sections/HookExecutableComponent';
import {Form, Tab, Tabs} from 'react-bootstrap';
import TeamSamePeriodTeamsTable from './TeamSamePeriodTeamsTable';
import {ageFormatter, injuryFormatter, ratingFormatter, salaryFormatter} from '../../common/Formatters';

const TeamSamePeriodTeams = (props: LevelDataPropsWrapper<TeamLevelDataProps>) => {
    const t = useTranslation().t

    const onChanged = (event: React.FormEvent<HTMLSelectElement>, setRequest: (request: CreatedSameTimeTeamRequest) => void) => {
        const periodString = event.currentTarget.value
        const split = periodString.split('_')

        if (split.length === 1) {
            setRequest({ period: split[0] as 'season' | 'round' })
        } else {
            setRequest( { period: split[0] as 'weeks', weeksNumber: Number(split[1])} )
        }
    }

    const content = (stateAndRequest: StateAndRequest<CreatedSameTimeTeamRequest, Array<CreatedSameTimeTeamExtended>>) => { 
        return <div className='table-responsive'>
            <Form className='d-flex flex-row align-items-center mb-2' max-width='200'>
                <span className='me-2 align-middle'>{t('team.period')}:</span>
                <Form.Select size='sm' defaultValue='season' style={{maxWidth: '200px'}}
                        onChange={e => onChanged(e, stateAndRequest.setRequest)}>
                    <option value="round">{t('chart.round')}</option>
                    <option value="season">{t('filter.season')}</option>
                    {Array.from(Array(16), (_, index) => index + 1).map(round => 
                        <option key={`week-${round}`} value={"weeks_" + round}>{t('filter.weeks_within').replace('#', round.toString())}</option>
                    )}
                </Form.Select>
            </Form>
            <Tabs id="same-periods-tabs">
                <Tab eventKey='powerRating' title={t('table.power_rating')}>
                    <TeamSamePeriodTeamsTable 
                        data={stateAndRequest.currentState}
                        levelDataPropsWrapper={props}
                        rowsWithTitle={[
                            {title: t('table.power_rating'), rowFunc: cstt => cstt.powerRating, valueFunc: cstt => cstt.powerRating}
                        ]}
                    />
                </Tab>
                <Tab eventKey='hatstats' title={t('table.hatstats')}>
                    <TeamSamePeriodTeamsTable
                        data={stateAndRequest.currentState}
                        levelDataPropsWrapper={props}
                        rowsWithTitle={[
                            {title: t('table.hatstats'), rowFunc: cstt => cstt.hatstats, valueFunc: cstt => cstt.hatstats},
                            {title: t('table.midfield'), rowFunc: cstt => cstt.midfield, valueFunc: cstt => cstt.midfield},
                            {title: t('table.defense'), rowFunc: cstt => cstt.defense, valueFunc: cstt => cstt.defense},
                            {title: t('table.attack'), rowFunc: cstt => cstt.attack, valueFunc: cstt => cstt.attack},
                        ]}
                    />
                </Tab>
                <Tab eventKey='tsi_salary' title={t('menu.team_salary_tsi')}>
                    <TeamSamePeriodTeamsTable 
                        data={stateAndRequest.currentState}
                        levelDataPropsWrapper={props}
                        rowsWithTitle={[
                            {title: t('table.tsi'), rowFunc: cstt => salaryFormatter(cstt.tsi), valueFunc: cstt => cstt.tsi},
                            {title: t('table.salary') + ', ' + props.levelDataProps.currency(), rowFunc: cstt => salaryFormatter(cstt.salary, props.levelDataProps.currencyRate()), valueFunc: cstt => cstt.salary},
                        ]}
                    />
                </Tab>
                 <Tab eventKey='ratings' title={t('menu.team_ratings')}>
                    <TeamSamePeriodTeamsTable 
                        data={stateAndRequest.currentState}
                        levelDataPropsWrapper={props}
                        rowsWithTitle={[
                            {title: t('table.rating'), rowFunc: cstt => ratingFormatter(cstt.rating), valueFunc: cstt => cstt.rating},
                            {title: t('table.rating_end_of_match'), rowFunc: cstt => ratingFormatter(cstt.ratingEndOfMatch), valueFunc: cstt => cstt.ratingEndOfMatch},
                        ]}
                    />
                </Tab>
                <Tab eventKey='age_injury' title={t('menu.team_age_injury')}>
                    <TeamSamePeriodTeamsTable 
                        data={stateAndRequest.currentState}
                        levelDataPropsWrapper={props}
                        rowsWithTitle={[
                            {title: t('table.age'), rowFunc: cstt => ageFormatter(Math.round(cstt.age)), valueFunc: cstt => cstt.age },
                            {title: t('table.injury'), rowFunc: cstt => injuryFormatter(cstt.injury), valueFunc: cstt => cstt.injury},
                        ]}
                    />
                </Tab>
            </Tabs>
            </div>
    }

    return <ExecutableComponent<CreatedSameTimeTeamRequest, Array<CreatedSameTimeTeamExtended>> 
        initialRequest={ {period: 'season'} }
        responseToState={response => response || []}
        content={content}
        executeRequest={(request, callback) => {
            getCreatedSameTimeTeams(props.levelDataProps.leagueId(), props.levelDataProps.foundedDate(),
                request, callback)
        }
        }
    />
}

export default TeamSamePeriodTeams
