import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { Sidebar, Segment, Container } from 'semantic-ui-react'

import '../styles/App.css';
import TopMenu from './TopMenu';
import Player from './Player';
import SidebarMenu from '../components/SidebarMenu';
import Home from './Home';
import Search from './Search';
import NoMatch from '../components/NoMatch';

const App = () => (
  <div>
    
    <TopMenu />

    <Container id='sidebar' >
      <Sidebar.Pushable>
        <SidebarMenu />
        <Sidebar.Pusher>
          <Segment basic id='mainContent' >
            <Switch>
              <Route exact path='/' component={Home} />
              <Route path='/home' component={Home} />
              <Route path='/search' component={Search} />
              <Route component={NoMatch} />
            </Switch>
          </Segment>
        </Sidebar.Pusher>
      </Sidebar.Pushable>
    </Container>

    <Player />

  </div>
);

export default App;