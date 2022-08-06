import React from 'react';
import './App.css';
import {  Route, BrowserRouter as Router, Routes } from 'react-router-dom'
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
import './main.css'
import Player from './player/Player';

class App extends React.Component{

  render() {
    return (<>
    <Router>
      <Routes>
        <Route path="/" element={<AboutLayout />} />
        <Route path="/about" element={<AboutLayout />} />
        <Route path="/worldOverview" element={<World />} />
        <Route path="/league" element={<LeagueRedirect />} />
        <Route path="/league/:leagueId" element={<League />} />
        <Route path="/league/:leagueId/divisionLevel/:divisionLevel"  element={<DivisionLevel />}/>
        <Route path="/leagueLevelUnit" element={<LeagueUnitRedirect />} />
        <Route path="/leagueUnit/:leagueUnitId" element={<LeagueUnit />}/>
        <Route path="/team/teamOverview" element={<TeamRedirect />}/>
        <Route path="/team/:teamId" element={<Team />}/>
        <Route path="/player/:playerId" element={<Player />}/>
      </Routes>
      </Router>
      <CookieWidget />
      </>
    );
  }
}

export default App;
