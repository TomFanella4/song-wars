import React from 'react';
import { Provider } from 'react-redux';
import { BrowserRouter, Route, Switch } from 'react-router-dom';

import App from './App';
import Auth from '../containers/Auth';

const Root = ({ store }) => (
  <Provider store={store}>
    <BrowserRouter>
      <Switch>
        <Route exact path='/auth' component={Auth} />
        <Route path='/' component={App} />
      </Switch>
    </BrowserRouter>
  </Provider>
);

export default Root;