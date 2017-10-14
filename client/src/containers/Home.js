import React, { Component } from 'react';
import { Button, Header } from 'semantic-ui-react';

import Bracket from './Bracket';
import { getAccessToken } from '../common';
import { authSpotify, authServer } from '../common/WebServices';

class App extends Component {

  render() {
    return (
      <div>
        <Header as='h1' content='Song Wars' />
        {!getAccessToken() &&
          <Button onClick={authSpotify}>
            Login with Spotify
          </Button>
        }
        <Bracket />
      </div>
    );
  }
}

export default App;
