import {useEffect} from 'react'
import { useNavigate } from 'react-router';


const LeagueRedirect = () => {   
    const navigate = useNavigate()

    useEffect(() => {
        const params = new URLSearchParams(window.location.search);
        let leagueId = params.get('leagueId')
        if (leagueId?.charAt(leagueId.length - 1) === ']') {
            leagueId = leagueId.slice(0, leagueId.length - 1)
        }
        navigate('/league/' + leagueId)
    })

    return <></>
}

export default LeagueRedirect
