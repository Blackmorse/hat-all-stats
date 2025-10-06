import { isValidElement, ReactNode, ReactElement } from 'react';
import { OverlayTrigger, Tooltip } from 'react-bootstrap';
import { OverlayInjectedProps } from 'react-bootstrap/esm/Overlay';

interface Props {
    poppedHint?: string;
    content: ReactNode;
}

const HattidTooltip = (props: Props) => {
    const renderTooltip = (toolTipProps: OverlayInjectedProps) => {
        if (props.poppedHint === undefined) {
            return <></>;
        }
        return (
            <Tooltip {...toolTipProps}>
                {props.poppedHint}
            </Tooltip>
        );
    };

    if (props.poppedHint === undefined) {
        return <>{props.content}</>;
    }

    let trigger = props.content;

    // OverlayTrigger needs a single child that can take a ref (a DOM element).
    // Wrap anything that is not a plain DOM element (string type) in a span.
    if (!isValidElement(trigger) || typeof trigger.type !== 'string') {
        trigger = <span className="hattid-tooltip-trigger">{trigger}</span>;
    }

    return (
        <OverlayTrigger
            placement='bottom'
            delay={{ show: 100, hide: 400 }}
            overlay={renderTooltip}
        >
            {trigger as ReactElement}
        </OverlayTrigger>
    );
};

export default HattidTooltip
