import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import { Card, Loader, Header } from 'semantic-ui-react';

import { getBracketHistoryHeaders } from '../api';

class BracketHistory extends Component {
  state = { brackets: [], loading: true };

  componentDidMount() {
    getBracketHistoryHeaders()
    .then(brackets => this.setState({ brackets: brackets.headers, loading: false }))
    .catch(err => this.setState({ loading: false }))
  }

  render() {
    const { brackets, loading } = this.state;
    // console.log(brackets);

    return (
      loading ?
        <Loader active={true} size='massive' />
      :
        brackets.length ?
          <div>
            {
              brackets.map(bracket => (
                <Link to={'/history/' + bracket.bracket_id} key={bracket.bracket_id}>
                  <Card header={bracket.date} link={true} />
                  <br />
                </Link>
              ))
            }
          </div>
        :
          <Header as='h2' color='grey'>No Brackets Available</Header>
    );
  }
}

export default BracketHistory;
