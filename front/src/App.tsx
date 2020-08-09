import React from 'react';
import './App.css';
import {  Route, BrowserRouter as Router } from 'react-router-dom'
import { League } from './league/League'



class App extends React.Component{


  
  render() {
    return (
      <Router>
        <Route exact path="/league/:leagueId" component={League} />
      </Router>
    );
  }
}

export default App;
