import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Dimmer, Loader } from 'semantic-ui-react';
import { Redirect } from 'react-router-dom';

import { setUserProfile } from '../actions';
import { saveUserProfile } from '../common'
import { authServer } from '../api';

class Auth extends Component {
  state = { authenticated: false };
  
  componentDidMount() {
    let uriSearch = this.props.location.search;
    if (uriSearch.includes('code')) {
      let start = uriSearch.indexOf('=') + 1;
      let code = uriSearch.substring(start);

      authServer(code)
      .then(userProfile => {
        saveUserProfile(userProfile);
        this.props.onUserAuthenticated(userProfile);
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

const mapDispatchToProps = dispatch => ({
  onUserAuthenticated: userProfile => dispatch(setUserProfile(userProfile))
});

export default connect(
  null,
  mapDispatchToProps
)(Auth);
