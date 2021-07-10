import React from 'react';
import './App.css';
import {  Route, BrowserRouter as Router, Switch } from 'react-router-dom'
import League from './league/League'
import DivisionLevel from './divisionlevel/DivisionLevel'
import LeagueUnit from './leagueunit/LeagueUnit'
import Team from './team/Team'
import World from './world/World';
import AboutLayout from './world/AboutLayout'
import CookieWidget from './CookieWidget'
import LeagueRedirect from './common/redirect/LeagueRedirect'
import LeagueUnitRedirect from './common/redirect/LeagueUnitRedirect'
import TeamRedirect from './common/redirect/TeamRedirect'

class App extends React.Component{

  render() {
    return (<>
    <Router>
      <Switch>
        <Route exact path="/" component={AboutLayout} />
        <Route exact path="/about" component={AboutLayout} />
        <Route exact path="/worldOverview" component={World} />
        <Route exact path="/league" component={LeagueRedirect} />
        <Route exact path="/league/:leagueId" component={League} />
        <Route exact path="/league/:leagueId/divisionLevel/:divisionLevel"  component={DivisionLevel}/>
        <Route exact path="/leagueLevelUnit" component={LeagueUnitRedirect} />
        <Route exact path="/leagueUnit/:leagueUnitId" component={LeagueUnit}/>
        <Route exact path="/team/teamOverview" component={TeamRedirect}/>
        <Route exact path="/team/:teamId" component={Team}/>
      </Switch>
      </Router>
      <CookieWidget />
      </>
    );
  }
}

export default App;
