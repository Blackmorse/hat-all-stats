import {useEffect} from 'react'
import {useNavigate} from 'react-router';

const LeagueUnitRedirect = () => {
    const navigate = useNavigate()

    useEffect(() => {
        const params = new URLSearchParams(window.location.search);
        const leagueUnitId = params.get('leagueUnitId')
        navigate('/leagueUnit/' + leagueUnitId)
    })
    return <></>
}

export default LeagueUnitRedirect
