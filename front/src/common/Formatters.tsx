import React from 'react';
import './Formatters.css'

export function commasSeparated(value: number): JSX.Element {
    return <>{value.toLocaleString()}</>
}

export function ageFormatter(value: number): JSX.Element {
    return <>{Math.floor(value / 112)},{value % 112}</>
}

export function ratingFormatter(value: number | undefined): JSX.Element {
    if (value === undefined) {
        return <></>
    }
    return <span className="rating">
        <span className="rating_container">
            <img className="star" src="/star.svg" alt="star"/>
            <span className="mult">x</span>
            <span className="rating">{(value / 10).toFixed(1)}</span>
        </span>
    </span>
}

export function injuryFormatter(value: number): JSX.Element {
    
    return <> 
        <svg width="10px" height="10px">
                <line x1="0" y1="5" x2="10" y2="5" className="injury_line"></line>
                <line x1="5" y1="0" x2="5" y2="10" className="injury_line"></line>
            </svg>
            {(value === -1) ? 0 : value}
        </>
}

export function yellowCards(value: number): JSX.Element {
    return <><span className="yellow card"></span>
        <span className="mult">x</span>
        {value}
        </>
}

export function redCards(value: number): JSX.Element {
    return <>
        <span className="red card"></span>
        <span className="mult">x</span>
        {value}
    </>
}