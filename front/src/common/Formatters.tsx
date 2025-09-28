import { type JSX } from 'react';
import moment from 'moment';
import './Formatters.css'

export function commasSeparated(value: number): JSX.Element {
    return <>{value.toLocaleString()}</>
}

export function stringCommasSeparated(value: number): string {
    return value.toLocaleString()
}

export function stringAgeFormatter(value?: number): string {
    if (value === undefined) return ''
    const age =  Math.floor(value / 112).toString() + '(' + (value % 112).toString() + ')'
    return age
}

export function ageFormatter(value?: number): JSX.Element {
    return <>{stringAgeFormatter(value)}</>
}

export function ratingFormatter(value: number | undefined): JSX.Element {
    if (value === undefined) {
        return <></>
    }
    return <span className='d-flex flex-row justify-content-center'>
            <img height='20px' src="/star.svg" alt="star"/>
            <span className='very-small-font align-self-end'>x</span>
            <span>{(value / 10).toFixed(1)}</span>
        </span>
}

export function injuryFormatter(value?: number): JSX.Element {
    if (value === undefined) return <></>
    return <> 
        <svg width="10px" height="10px" style={{fill: 'rgb(0,0,255)', strokeWidth: '3', stroke: 'rgb(255,0,0)'}}>
                <line x1="0" y1="5" x2="10" y2="5"></line>
                <line x1="5" y1="0" x2="5" y2="10"></line>
            </svg>
            {(value === -1) ? 0 : value}
        </>
}

export function yellowCards(value: number): JSX.Element {
    return <><i className='bi bi-file-fill player_card_rotate' style={{color: 'yellow', transform: 'rotate(20deg)'}}></i>
        <span className="very-small-font">x</span>
        {value}
        </>
}

export function redCards(value: number): JSX.Element {
    return <>
        <i className='bi bi-file-fill player_card_rotate' style={{color: 'red', transform: 'rotate(20deg)'}}></i>
        <span className="very-small-font">x</span>
        {value}
    </>
}

export function loddarStats(value: number): JSX.Element {
    return <>{Math.ceil(value * 100) / 100}</>
}

export function stringSalaryFormatter(value?: number, currencyRate?: number): string {
    if (value === undefined) return ''
    return stringCommasSeparated(Math.floor(value / ((currencyRate === undefined) ? 1 : currencyRate)))
}

export function salaryFormatter(value?: number, currencyRate?: number): JSX.Element {
    return <>{stringSalaryFormatter(value, currencyRate)}</>
}

export function doubleSalaryFormatter(value: number, currencyRate?: number): number {
    const val = value / ((currencyRate === undefined) ? 1 : currencyRate)
    return Math.ceil(val * 100) / 100
}

export function dateFormatter(value: Date): JSX.Element {
    return <>{moment(value).format('DD.MM.YYYY')}</>
}

export function dateNumberFormatter(value: number): JSX.Element {
    return <>{moment(value).format('DD.MM.YYYY')}</>
}
