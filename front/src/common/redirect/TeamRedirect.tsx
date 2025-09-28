import {useNavigate} from "react-router";
import {useEffect} from 'react'

const TeamRedirect = () => { 
    const navigate = useNavigate()
    useEffect(() => {
        const params = new URLSearchParams(window.location.search);
        const teamId = params.get('teamId')
        navigate('/team/' + teamId)
    })
    return <></>
}

export default TeamRedirect
