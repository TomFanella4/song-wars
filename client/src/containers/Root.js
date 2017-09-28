import React from 'react';
import { Provider } from 'react-redux';
import { BrowserRouter, Route } from 'react-router-dom';
import { Sidebar, Segment } from 'semantic-ui-react'

import '../styles/Root.css';
import TopMenu from './TopMenu';
import SidebarMenu from '../components/SidebarMenu';
import Home from './Home';
import Search from './Search'

const Root = ({ store }) => (
  <Provider store={store}>
    <BrowserRouter>
      <div>
        
        <TopMenu />

        <Sidebar.Pushable>
          <SidebarMenu />
          <Sidebar.Pusher>
            <Segment basic style={{overflowX: 'auto'}}>
              <Route exact path='/' component={Home}/>
              <Route path='/search' component={Search}/>
            </Segment>
          </Sidebar.Pusher>
        </Sidebar.Pushable>
      </div>
    </BrowserRouter>
  </Provider>
);

export default Root;