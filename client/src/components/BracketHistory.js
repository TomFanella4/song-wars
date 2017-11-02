import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import { Card } from 'semantic-ui-react';

import { getBracketHistoryHeaders } from '../api';

class BracketHistory extends Component {
  state = { brackets: [] };

  componentDidMount() {
    getBracketHistoryHeaders()
    .then(brackets => this.setState({ brackets: brackets.headers }))
    .catch(err => console.error(err))
  }

  render() {
    const { brackets } = this.state;

    return (
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
    );
  }
}

export default BracketHistory;
