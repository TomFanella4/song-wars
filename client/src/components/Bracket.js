import React, { Component } from 'react';
import { Button, Transition, Segment } from 'semantic-ui-react';

import BracketUI from '../containers/BracketUI';
import Vote from '../containers/Vote';

class Bracket extends Component {
  state = { view: { bracket: true, vote: false } }

  changeView() {
    const { bracket } = this.state.view;

    bracket ?
      this.setState({ view: { ...this.state.view, bracket: false } })
    :
      this.setState({ view: { ...this.state.view, vote: false } })
  }

  render() {
    // Add transition here after D3
    const { bracket, vote } = this.state.view;
    return (
      <div>
        <Button content={vote ? 'Bracket' : 'Vote'} onClick={this.changeView.bind(this)} />
        <Segment basic style={{ textAlign: '-webkit-center' }}>
          <Transition
            animation='scale'
            visible={bracket}
            onHide={() => this.setState({ view: { ...this.state.view, vote: true } })}
          >
            <Transition.Group>
              <BracketUI />
            </Transition.Group>
          </Transition>
          <Transition
            animation='scale'
            visible={vote}
            onHide={() => this.setState({ view: { ...this.state.view, bracket: true } })}
          >
            <Transition.Group>
              <Vote />
            </Transition.Group>
          </Transition>
        </Segment>
      </div>
    );
  }
}

export default Bracket;
