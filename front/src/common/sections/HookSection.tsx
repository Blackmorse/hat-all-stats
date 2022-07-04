import React, {useState} from 'react'
import {Card} from 'react-bootstrap'
import {Link} from 'react-router-dom'

interface Props {
    element: JSX.Element
    title: string | JSX.Element
}

const Section = (props: Props) => {
    const [collapsed, setCollapsed] = useState(false)

    let triangle = (!collapsed) ? <i className="bi bi-caret-down-fill"></i> : <i className="bi bi-caret-right-fill"></i>
   return <Card className="shadow-sm">
            <Card.Header className="lead text-start"  onClick={() => setCollapsed(!collapsed)}>
                {triangle} <Link to='#' className="link-dark section-cursor"> {props.title}</Link> 
            </Card.Header>
            <Card.Body className="p-2">
                <span className={(collapsed) ? 'd-none' : ''}>
                    {props.element}
                </span>
            </Card.Body>
        </Card> 
}

export default Section
