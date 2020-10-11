import React from 'react';
import {ClipLoader} from "react-spinners";
import './Blur.css'

interface Props {
    dataLoading: boolean
}

class Blur extends React.Component<Props> {
    render() {
        if(this.props.dataLoading) {

                return <div className="blur">
                <div className="blur_loader">
                    <ClipLoader
                        size={"100px"}
                        color={"#123abc"}
                        loading={true}
                    />
                </div>
            </div>
        } else {
            return <></>
        }
    }
}

export default Blur