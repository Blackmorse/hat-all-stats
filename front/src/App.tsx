import React from 'react';
import './App.css';
import {  Route, BrowserRouter as Router } from 'react-router-dom'
import League from './league/League'
import DivisionLevel from './divisionlevel/DivisionLevel'
import LeagueUnit from './leagueunit/LeagueUnit'

class App extends React.Component{

  render() {
    return (
      <Router>
        <Route exact path="/league/:leagueId" component={League} />
        <Route exact path="/league/:leagueId/divisionLevel/:divisionLevel"  component={DivisionLevel}/>
        <Route exact path="/leagueUnit/:leagueUnitId" component={LeagueUnit}/>
      </Router>
    );
  }
}

export default App;
