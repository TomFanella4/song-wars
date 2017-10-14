import React, { Component } from 'react';
import { Segment, Dimmer, Loader } from 'semantic-ui-react';
import { Redirect } from 'react-router-dom';

import { authServer } from '../common/WebServices';

class Auth extends Component {
  state = { authenticated: false };
  
  componentDidMount() {
    let uriSearch = this.props.location.search;
    if (uriSearch.includes('code')) {
      let start = uriSearch.indexOf('=') + 1;
      let code = uriSearch.substring(start);

      authServer(code)
      .then(profile => {
        localStorage.setItem('access_token', profile.access_token);
        localStorage.setItem('name', profile.name);
        localStorage.setItem('user_id', profile.user_id);
      })
      .catch(err => console.error(err))
      .then(() => {
        this.setState({ authenticated: true });
      })
    } else {
      this.setState({ authenticated: true });
    }
  }

  render() {
    return (
      <div>
        {!this.state.authenticated ?
          <Dimmer active>
            <Loader size='large'>Logging In...</Loader>
          </Dimmer>
        :
          <Redirect to='/' />
        }
      </div>
    );
  }
};

export default Auth;