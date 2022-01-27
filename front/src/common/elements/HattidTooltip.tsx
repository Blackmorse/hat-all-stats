import React from 'react';
import {OverlayTrigger, Tooltip} from 'react-bootstrap';
import {OverlayInjectedProps} from 'react-bootstrap/esm/Overlay';

interface Props {
    poppedHint?: string,
    content: JSX.Element
}

const HattidTooltip = (props: Props) => {
     const renderTooltip = (toolTipProps: OverlayInjectedProps) => {
        if(props.poppedHint === undefined) {
            return <></>
        }
        return <Tooltip {...toolTipProps}>
            {props.poppedHint}
        </Tooltip>
    }

    if (props.poppedHint === undefined) {
        return props.content
    }

    return <OverlayTrigger 
                placement='bottom'
                delay={{ show: 100, hide: 400 }}
                overlay={renderTooltip}
            >
                {props.content}
        </OverlayTrigger>
}

export default HattidTooltip
