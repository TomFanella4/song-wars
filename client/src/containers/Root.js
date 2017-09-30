import React from 'react';
import { Provider } from 'react-redux';
import { BrowserRouter, Route } from 'react-router-dom';
import { Sidebar, Segment } from 'semantic-ui-react'

import TopMenu from './TopMenu';
import SidebarMenu from '../components/SidebarMenu';
import App from './App';
import Search from './Search'

const Root = ({ store }) => (
  <Provider store={store}>
    <BrowserRouter>
      <div>
        
        <TopMenu />

        <Sidebar.Pushable>
          <SidebarMenu />
          <Sidebar.Pusher>
            <Segment basic>
              <Route exact path='/' component={App}/>
              <Route path='/search' component={Search}/>
            </Segment>
          </Sidebar.Pusher>
        </Sidebar.Pushable>
      </div>
    </BrowserRouter>
  </Provider>
);

export default Root;