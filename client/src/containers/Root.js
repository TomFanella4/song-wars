import React from 'react';
import { Provider } from 'react-redux';
import { BrowserRouter, Link, Route } from 'react-router-dom';

import App from './App';
import Search from './Search'

const Root = ({ store }) => (
  <Provider store={store}>
    <BrowserRouter>
      <div>
        <nav>
          <Link to='/'>Home</Link>
          <Link to='/search'>Search</Link>
        </nav>
        <div>
          <Route exact path='/' component={App}/>
          <Route path='/search' component={Search}/>
        </div>
      </div>
    </BrowserRouter>
  </Provider>
);

export default Root;