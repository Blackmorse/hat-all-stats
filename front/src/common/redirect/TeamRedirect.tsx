import {useNavigate} from "react-router";
import React, {useEffect} from 'react'

const TeamRedirect = () => { 
    const navigate = useNavigate()
    useEffect(() => {
        let params = new URLSearchParams(window.location.search);
        let teamId = params.get('teamId')
        navigate('/team/' + teamId)
    })
    return <></>
}

export default TeamRedirect
