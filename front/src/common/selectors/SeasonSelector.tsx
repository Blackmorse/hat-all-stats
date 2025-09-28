import React from 'react'
import '../../i18n'
import { useTranslation } from 'react-i18next'
import { Form } from 'react-bootstrap'

interface SeasonSelectorProps {
    currentSeason: number,
    seasonOffset: number
    seasons: Array<number>,
    all?: boolean,
    callback: (season: number) => void // -1 for "all"
}

const SeasonSelector = (props: SeasonSelectorProps) => {
    const [ t, _i18n ] = useTranslation()

    const onChanged = (event: React.FormEvent<HTMLSelectElement>) => {
        if (event.currentTarget.value === "all") {
            props.callback(-1)
        } else {
            const season = Number(event.currentTarget.value)
            props.callback(season)
        }
    }

    return <div className='d-flex flex-row align-items-center mx-2  my-xs-2 my-sm-2 my-lg-0 my-md-0'>
        <span className="me-1">{t('filter.season')}:</span>
        <Form.Select size='sm' defaultValue={(props.currentSeason === -1) ? "all" : props.currentSeason}
                     onChange={onChanged}>
            {props.seasons.map(season => {
                return <option key={"select_season_" + season}
                               value={season}>{season + props.seasonOffset}</option>
            })}
            {(props.all) &&
                <option key={"select_season_all"} value="all">{t("selectors.all")}</option>
            }
        </Form.Select>
    </div>
}

export default SeasonSelector
