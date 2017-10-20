import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Segment, Button, Transition, Divider } from 'semantic-ui-react';

import { setPlayerURI } from '../actions';

class Vote extends Component {
  state = { animation: 'fly left' , group1: true, group2: false }

  handleNextClick() {
    this.state.group1 ?
    this.setState({ group1: !this.state.group1 })
    :
    this.setState({ group2: !this.state.group2 })
  }

  render() {
    const { animation, group1, group2 } = this.state;
    return (
      <Segment compact>
      
      <Transition animation='fly left' visible={group1} onHide={() => this.setState({ group2: true })}>
        <Transition.Group>
            <div>
              <Button.Group size='massive'>
                <Button color='red' content='Gem1' />
                <Button.Or />
                <Button color='green' content='Popular1' />
              </Button.Group>
            </div>
          </Transition.Group>
        </Transition>

        <Transition animation='fly left' visible={group2} onHide={() => this.setState({ group1: true })}>
          <Transition.Group>
            <div>
              <Button.Group size='massive'>
                <Button color='red' content='Gem2' />
                <Button.Or />
                <Button color='green' content='Popular2' />
              </Button.Group>
            </div>
          </Transition.Group>
        </Transition>

        <Divider />

        <Button
          content='Next'
          icon='right arrow'
          labelPosition='right'
          onClick={this.handleNextClick.bind(this)}
        />
      </Segment>
    );
  }
}

const mapDispatchToProps = dispatch => ({
  setPlayerURI: uri => dispatch(setPlayerURI(uri))
});

export default connect(
  null,
  mapDispatchToProps
)(Vote);
