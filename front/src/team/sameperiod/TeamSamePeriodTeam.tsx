import React from 'react'
import CreatedSameTimeTeamExtended from '../../rest/models/team/CreatedSameTimeTeamExtended';
import { useTranslation } from 'react-i18next'
import { LevelDataPropsWrapper } from '../../common/LevelDataProps'
import TeamData from '../../rest/models/leveldata/TeamData'
import TeamLevelDataProps from '../TeamLevelDataProps'
import { getCreatedSameTimeTeams } from '../../rest/Client'
import ExecutableComponent from '../../common/sections/HookExecutableComponent';
import {Form, Tab, Tabs} from 'react-bootstrap';
import TeamSamePeriodTeamsTable from './TeamSamePeriodTeamsTable';
import {ageFormatter, injuryFormatter, ratingFormatter, salaryFormatter} from '../../common/Formatters';

const TeamSamePeriodTeams = (props: LevelDataPropsWrapper<TeamData, TeamLevelDataProps>) => {
    const t = useTranslation().t

    const onChanged = (event: React.FormEvent<HTMLSelectElement>, setRequest: (request: string) => void) => {
        let period = event.currentTarget.value
        setRequest(period)
    }

    const content = (setRequest: (request: string) => void, data?: Array<CreatedSameTimeTeamExtended>) => { 
        return <div className='table-responsive'>
            <Form className='d-flex flex-row align-items-center mb-2' max-width='200'>
                <span className='me-2 align-middle'>{t('team.period')}:</span>
                <Form.Select size='sm' defaultValue='season' style={{maxWidth: '200px'}}
                        onChange={e => onChanged(e, setRequest)}>
                    <option value="round">{t('chart.round')}</option>
                    <option value="season">{t('filter.season')}</option>

                </Form.Select>
            </Form>
            <Tabs id="same-periods-tabs">
                <Tab eventKey='powerRating' title={t('table.power_rating')}>
                    <TeamSamePeriodTeamsTable 
                        data={data}
                        levelDataPropsWrapper={props}
                        rowsWithTitle={[
                            {title: t('table.power_rating'), rowFunc: cstt => cstt.powerRating, valueFunc: cstt => cstt.powerRating}
                        ]}
                    />
                </Tab>
                <Tab eventKey='tsi_salary' title={t('menu.team_salary_tsi')}>
                    <TeamSamePeriodTeamsTable 
                        data={data}
                        levelDataPropsWrapper={props}
                        rowsWithTitle={[
                            {title: t('table.tsi'), rowFunc: cstt => salaryFormatter(cstt.tsi), valueFunc: cstt => cstt.tsi},
                            {title: t('table.salary') + ', ' + props.levelDataProps.currency(), rowFunc: cstt => salaryFormatter(cstt.salary, props.levelDataProps.currencyRate()), valueFunc: cstt => cstt.salary},
                        ]}
                    />
                </Tab>
                 <Tab eventKey='ratings' title={t('menu.team_ratings')}>
                    <TeamSamePeriodTeamsTable 
                        data={data}
                        levelDataPropsWrapper={props}
                        rowsWithTitle={[
                            {title: t('table.rating'), rowFunc: cstt => ratingFormatter(cstt.rating), valueFunc: cstt => cstt.rating},
                            {title: t('table.rating_end_of_match'), rowFunc: cstt => ratingFormatter(cstt.ratingEndOfMatch), valueFunc: cstt => cstt.ratingEndOfMatch},
                        ]}
                    />
                </Tab>
                <Tab eventKey='age_injury' title={t('menu.team_age_injury')}>
                    <TeamSamePeriodTeamsTable 
                        data={data}
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

    return <ExecutableComponent<string, Array<CreatedSameTimeTeamExtended>> 
        initialRequest='season'
        content={content}
        executeRequest={(request, callback) => {
            getCreatedSameTimeTeams(props.levelDataProps.leagueId(), props.levelDataProps.levelData.foundedDate,
                request, callback)
        }
        }
    />
}

export default TeamSamePeriodTeams
