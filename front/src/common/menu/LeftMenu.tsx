import React from 'react';
import './LeftMenu.css'
import { PagesEnum } from '../enums/PagesEnum'
import { MenuGroupsEnum } from '../enums/MenuGroupsEnum'
import { Translation } from 'react-i18next'
import '../../i18n'

interface Props {
    callback: (page: PagesEnum) => void;
    pages: Array<PagesEnum>
}

interface State {
    stateMap: Map<MenuGroupsEnum, boolean>
    groupsMap:  Map<MenuGroupsEnum, Array<PagesEnum>>
}

class LeftMenu extends React.Component<Props, State> {
    static groupMap: Map<PagesEnum, MenuGroupsEnum> = 
        new Map([[PagesEnum.LEAGUE_UNITS, MenuGroupsEnum.LEAGUE],
         [PagesEnum.TEAM_HATSTATS, MenuGroupsEnum.TEAM],
         [PagesEnum.TEAM_OVERVIEW, MenuGroupsEnum.TEAM],
         [PagesEnum.PLAYER_GOAL_GAMES, MenuGroupsEnum.PLAYER],
         [PagesEnum.PLAYER_CARDS, MenuGroupsEnum.PLAYER],
         [PagesEnum.PLAYER_SALARY_TSI, MenuGroupsEnum.PLAYER],
         [PagesEnum.PLAYER_RATINGS, MenuGroupsEnum.PLAYER],
         [PagesEnum.PLAYER_INJURIES, MenuGroupsEnum.PLAYER],
         [PagesEnum.TEAM_SALARY_TSI, MenuGroupsEnum.TEAM],
         [PagesEnum.TEAM_CARDS, MenuGroupsEnum.TEAM],
         [PagesEnum.TEAM_RATINGS, MenuGroupsEnum.TEAM],
         [PagesEnum.TEAM_AGE_INJURY, MenuGroupsEnum.TEAM],
         [PagesEnum.TEAM_GOAL_POINTS, MenuGroupsEnum.TEAM],
         [PagesEnum.TEAM_POWER_RATINGS, MenuGroupsEnum.TEAM],
         [PagesEnum.TEAM_FANCLUB_FLAGS, MenuGroupsEnum.TEAM],
         [PagesEnum.TEAM_STREAK_TROPHIES, MenuGroupsEnum.TEAM],
         [PagesEnum.MATCH_TOP_HATSTATS, MenuGroupsEnum.MATCH],
         [PagesEnum.MATCH_SURPRISING, MenuGroupsEnum.MATCH],
         [PagesEnum.MATCH_SPECTATORS, MenuGroupsEnum.MATCH]
        ])

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
                let group = LeftMenu.groupMap.get(page)
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
            <div className="left_side_inner">
                <div className="left_bar">
                    <header className="left_bar_header">{t('menu.statistics')}</header>
                    <section className="left_bar_links">
                        {Array.from(groups.keys()).map(group => {
                            return <>
                                <button className="left_bar_link group" onClick={() => this.showHide(group)}>
                                    {(stateMap.get(group) ? down : right)} {t(group)}
                                </button>
                                {(stateMap.get(group)) ? groups.get(group)?.map(page => {
                                    return <button className="left_bar_link page" onClick={() => this.props.callback(page)}>{t(page)}</button>
                                }) : <></>}
                            </>
                        })}
                    </section>
                </div>
            </div>
        }
        </Translation>
    }
}

export default LeftMenu