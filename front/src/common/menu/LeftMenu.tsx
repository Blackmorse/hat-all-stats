import React from 'react';
import './LeftMenu.css'
import { PagesEnum } from '../enums/PagesEnum'
import { MenuGroupsEnum } from '../enums/MenuGroupsEnum'
import { Translation } from 'react-i18next'
import '../../i18n'
import Mappings from '../enums/Mappings'
import { Card } from 'react-bootstrap';

interface Props {
    callback: (page: PagesEnum) => void;
    pages: Array<PagesEnum>,
    title: string
}

interface State {
    stateMap: Map<MenuGroupsEnum, boolean>
    groupsMap:  Map<MenuGroupsEnum, Array<PagesEnum>>
}

class LeftMenu extends React.Component<Props, State> {
    constructor(props: Props) {
        super(props)
        let groupsMap = this.groupBy(props.pages)
        let stateMap = new Map(Array.from(groupsMap.keys()).map(key => [key, true]))

        this.state = {
            stateMap: stateMap,
            groupsMap: groupsMap
        }
    }

    private groupBy(pages: Array<PagesEnum>): Map<MenuGroupsEnum, Array<PagesEnum>> {
        let map = new Map<MenuGroupsEnum, Array<PagesEnum>>()
              
        var i: number
        for(i = 0; i < pages.length; i++) {
            let page = pages[i]
            if (pages.indexOf(page) > -1) {
                let group = Mappings.groupMap.get(page)
                if (!group) {
                    continue
                }
                let currentGroup = map.get(group)
                if(currentGroup) {
                    currentGroup.push(page)
                } else {
                    map.set(group, [page])
                }
            }
        }

        return map
    }

    showHide(group: MenuGroupsEnum) {
        let newMap = new Map(this.state.stateMap)
        let oldValue: boolean
        let mapValue = this.state.stateMap.get(group)
        oldValue = (mapValue) ? mapValue : false

        newMap.set(group, !oldValue)

        this.setState({
            stateMap: newMap,
            groupsMap: this.state.groupsMap
        })
    }

    render() {     
        let groups = this.state.groupsMap
        let stateMap = this.state.stateMap

        let down = "▾"
        let right = "▸"
        
        return <Translation>{
            (t, { i18n }) =>
            <Card className="mt-3 shadow">
                <Card.Header className="lead">{t(this.props.title)}</Card.Header>
                <Card.Body>
                    {Array.from(groups.keys()).map((group, indexGroup) => {
                        return <>
                            <button className="btn btn-toggle align-items-center rounded collapsed ps-0 pb-0"
                                onClick={() => this.showHide(group)}>
                                {(stateMap.get(group) ? down : right)} {t(group)}
                            </button>
                            {/* TODO bootstrap small screen: text overflow to the left  */}
                            <div className='collapse show' id={indexGroup + '_collapse'}>
                                <ul className="btn-toggle-nav list-unstyled fw-normal pb-1 mb-1 small"> 
                                    {(stateMap.get(group)) ? groups.get(group)?.map(page => {
                                        return <li><a href="#" className="link-dark rounded ms-4"
                                            onClick={() => this.props.callback(page)}>{t(page)}</a></li>
                                    }) : <></>}
                                </ul>
                            </div>
                        </>
                    })}
                </Card.Body>
            </Card>
        }
        </Translation>
    }
}

export default LeftMenu