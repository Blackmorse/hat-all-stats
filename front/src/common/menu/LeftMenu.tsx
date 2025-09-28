import React, {Fragment} from 'react';
import { PagesEnum } from '../enums/PagesEnum'
import { MenuGroupsEnum } from '../enums/MenuGroupsEnum'
import { Translation } from 'react-i18next'
import '../../i18n'
import Mappings from '../enums/Mappings'
import { Card } from 'react-bootstrap';
import { Link } from 'react-router-dom';

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
        const groupsMap = this.groupBy(props.pages)
        const stateMap = new Map(Array.from(groupsMap.keys()).map(key => [key, true]))

        this.state = {
            stateMap: stateMap,
            groupsMap: groupsMap
        }
    }

    private groupBy(pages: Array<PagesEnum>): Map<MenuGroupsEnum, Array<PagesEnum>> {
        const map = new Map<MenuGroupsEnum, Array<PagesEnum>>()
              
        let i: number
        for(i = 0; i < pages.length; i++) {
            const page = pages[i]
            if (pages.indexOf(page) > -1) {
                const group = Mappings.groupMap.get(page)
                if (!group) {
                    continue
                }
                const currentGroup = map.get(group)
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
        const newMap = new Map(this.state.stateMap)
        const mapValue = this.state.stateMap.get(group)
        const oldValue = (mapValue) ? mapValue : false

        newMap.set(group, !oldValue)

        this.setState({
            stateMap: newMap,
            groupsMap: this.state.groupsMap
        })
    }

    render() {     
        const groups = this.state.groupsMap
        const stateMap = this.state.stateMap

        const down = "▾"
        const right = "▸"
        
        return <Translation>{
            (t) =>
            <Card className="mb-3 shadow">
                <Card.Header className="lead">{t(this.props.title)}</Card.Header>
                <Card.Body>
                    {Array.from(groups.keys()).map((group, indexGroup) => {
                        return <Fragment key={'left_menu_' + indexGroup}>
                            <button className="btn btn-toggle align-items-center rounded collapsed ps-0 pb-0"
                                onClick={() => this.showHide(group)}>
                                {(stateMap.get(group) ? down : right)} {t(group)}
                            </button>
                            <div className='collapse show' id={indexGroup + '_collapse'}>
                                <ul className="btn-toggle-nav list-unstyled fw-normal pb-1 mb-1 small ms-4"> 
                                    {(stateMap.get(group)) ? groups.get(group)?.map(page => {
                                        return <li key={'left_menu_' + indexGroup + '_' + page}><Link to="#" className="left-menu-link link-dark rounded"
                                            onClick={() => this.props.callback(page)}>{t(page)}</Link></li>
                                    }) : <></>}
                                </ul>
                            </div>
                        </Fragment>
                    })}
                </Card.Body>
            </Card>
        }
        </Translation>
    }
}

export default LeftMenu
