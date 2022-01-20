import React, {useEffect} from 'react'
import {useNavigate} from 'react-router';

const LeagueUnitRedirect = () => {
    const navigate = useNavigate()

    useEffect(() => {
        let params = new URLSearchParams(window.location.search);
        let leagueUnitId = params.get('leagueUnitId')
        navigate('/leagueUnit/' + leagueUnitId)
    })
    return <></>
}

export default LeagueUnitRedirect
