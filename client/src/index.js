import React from 'react';
import ReactDOM from 'react-dom';
import { createStore } from 'redux';

import 'semantic-ui-css/semantic.min.css';
import './styles/index.css';
import Root from './containers/Root';
import rootReducer from './reducers';
import registerServiceWorker from './registerServiceWorker';

let store = createStore(rootReducer);

ReactDOM.render(
  <Root store={store}></Root>,
  document.getElementById('root'));

registerServiceWorker();
