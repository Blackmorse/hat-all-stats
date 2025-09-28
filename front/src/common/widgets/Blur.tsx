import React, { type JSX } from 'react';
import {ClipLoader} from "react-spinners";
import './Blur.css'
import { Translation } from 'react-i18next'
import '../../i18n'
import { LoadingEnum } from '../enums/LoadingEnum';

interface Props {
    loadingState: LoadingEnum,
    updateCallback: () => void
}

class Blur extends React.Component<Props> {
    render() {
        let content: JSX.Element
        if (this.props.loadingState === LoadingEnum.ERROR) {
            content = <Translation>{
                (t) =>
                    <span className="blur_error">{t('error.loading')} 
                        <span className="table_link" onClick={this.props.updateCallback}>
                            <img className="reload_img" src="/reload.svg" alt="Reload" />
                        </span>
                    </span>
                }
                </Translation>
        } else if(this.props.loadingState === LoadingEnum.LOADING) {
            content = <ClipLoader
                size={"100px"}
                color={"#123abc"}
                loading={true}
            />
        } else {
            return <></>
        }

        if(this.props.loadingState === LoadingEnum.ERROR || this.props.loadingState === LoadingEnum.LOADING) {
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
