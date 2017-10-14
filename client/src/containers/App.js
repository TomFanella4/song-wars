import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { Sidebar, Segment } from 'semantic-ui-react'

import '../styles/Root.css';
import TopMenu from './TopMenu';
import SidebarMenu from '../components/SidebarMenu';
import Home from './Home';
import Search from './Search';
import NoMatch from '../components/NoMatch';

const App = () => (
  <div>
    
    <TopMenu />

    <Sidebar.Pushable>
      <SidebarMenu />
      <Sidebar.Pusher>
        <Segment basic style={{overflowX: 'auto'}}>
          <Switch>
            <Route exact path='/' component={Home}/>
            <Route path='/home' component={Home} />
            <Route path='/search' component={Search}/>
            <Route component={NoMatch} />
          </Switch>
        </Segment>
      </Sidebar.Pusher>
    </Sidebar.Pushable>
  </div>
);

export default App;