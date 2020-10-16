import React from 'react';
import {ClipLoader} from "react-spinners";
import './Blur.css'
import { Translation } from 'react-i18next'
import '../../i18n'

interface Props {
    dataLoading: boolean,
    isError: boolean,
    updateCallback: () => void
}

class Blur extends React.Component<Props> {
    render() {
        let content: JSX.Element
        if (this.props.isError) {
            content = <Translation>{
                (t, { i18n }) =>
                    <span className="blur_error">{t('error.loading')} 
                        <span className="table_link" onClick={this.props.updateCallback}>
                            <img className="reload_img" src="/reload.svg" alt="Reload" />
                        </span>
                    </span>
                }
                </Translation>
        } else if(this.props.dataLoading) {
            content = <ClipLoader
                size={"100px"}
                color={"#123abc"}
                loading={true}
            />
        } else {
            return <></>
        }

        if(this.props.dataLoading || this.props.isError) {
                return <div className="blur">
                <div className="blur_loader">
                    {content}
                </div>
            </div>
        } else {
            return <></>
        }
    }
}

export default Blur