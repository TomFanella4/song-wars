import React, { Component } from 'react';
import { Button } from 'semantic-ui-react';

import Bracket from '../containers/Bracket';
import Vote from '../containers/Vote';

class App extends Component {
  state = { vote: false }

  render() {
    // Add transition here after D3
    const { vote } = this.state;
    return (
      <div>
        <Button content={vote ? 'Bracket' : 'Vote'} onClick={() => this.setState({ vote: !vote })} />
        {
          vote ?
          <Vote />
          :
          <Bracket />
        }
      </div>
    );
  }
}

export default App;
