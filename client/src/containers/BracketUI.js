import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Header, Button, Card, Loader } from 'semantic-ui-react';
import $ from 'jquery';

import { popularCutoff } from '../common';
import { getCurrentBracket, getCurrentVotes } from '../api';
import { setBracketId, setVoteList } from '../actions';
import VoteOption from './VoteOption';

window.jQuery = $;

require('../../node_modules/jquery-bracket/dist/jquery.bracket.min.css');
require('../../node_modules/jquery-bracket/dist/jquery.bracket.min.js');

class BracketUI extends Component {
  state = { preview1: null, preview2: null, winner: null, loading: true }

  onMatchHover(cell) {
    cell && this.setState({ preview1: cell.Option1, preview2: cell.Option2 })
  }

  componentDidMount() {
    const { setBracketId, setVoteList } = this.props;

    getCurrentBracket()
    .then(bracket => {
      console.log(bracket)

      const leftBracket = {
        teams: bracket.RightSide[0].map(cell => [cell.Option1.name, cell.Option2.name]),
        results: bracket.RightSide.map(col => col.map(cell => [cell.Option1.votes, cell.Option2.votes, cell]))
      };

      const rightBracket = {
        teams: bracket.LeftSide[0].map(cell => [cell.Option1.name, cell.Option2.name]),
        results: bracket.LeftSide.map(col => col.map(cell => [cell.Option1.votes, cell.Option2.votes, cell]))
      };

      if (bracket.Winner) {
        this.setState({ winner: bracket.Winner });
      }

      // var data = {
      //   teams : [
      //     ["Piano Man",  "Feels" ],
      //     ["Batman Theme",  "Scarborough Fair" ],
      //     ["Believer",  "Look What You Made Me Do" ],
      //     ["I Don’t Wanna Live Forever",  "Radioactive" ]
      //   ],
      //   results : [
      //     [[1,2], [3,4], [5,6], [7,8]],
      //     [[9,20], [7,2]],
      //     [[40,3]]
      //   ]
      // };
      
      this.setState({ loading: false });
      
      $('.leftBracket').bracket({
        skipConsolationRound: true,
        teamWidth: 100,
        matchMargin: 40,
        onMatchHover: this.onMatchHover.bind(this),
        init: leftBracket
      });
  
      $('.rightBracket').bracket({
        skipConsolationRound: true,
        teamWidth: 100,
        matchMargin: 40,
        onMatchHover: this.onMatchHover.bind(this),
        dir: 'rl',
        init: rightBracket
      });

      setBracketId(bracket.BracketId);

      getCurrentVotes(bracket.BracketId)
      .then(list => setVoteList(list))
      .catch(err => console.error(err))
    })
    .catch(err => console.error(err));
  }

  render() {
    const { preview1, preview2, winner, loading } = this.state;

    return (
      loading ?
        <Loader active={true} size='massive' style={{ marginTop: 200 }} />
      :
        <div>
          <div style={{ display: 'flex' }}>
            <div>
              <Header as='h1' content='Hidden Gems' icon='diamond' color='red' />
              <span className="leftBracket" />
            </div>
            <span style={{ width: 175, lineHeight: 8 }}>
              <Header content='VS' style={{ fontSize: 75 }} />
              {
                winner ?
                  <Header
                    as='h1'
                    content={'Winner:\n' + winner.name}
                    color={winner.popularity < popularCutoff ? 'red' : 'green'}
                  />
                :
                  <Button content='Vote' color='green' size='huge' onClick={this.props.changeView} />
              }
            </span>
            <div style={{ marginLeft: 40 }}>
              <Header as='h1' content='Popular' icon='star' color='green' />
              <span className="rightBracket" />
            </div>
          </div>
          <Card.Group style={{ justifyContent: 'center' }}>
            {preview1 && <VoteOption song={preview1} />}
            {preview2 && <VoteOption song={preview2} />}
          </Card.Group>
        </div>
    );
  }
}

const mapDispatchToProps = dispatch => ({
  setBracketId: id => dispatch(setBracketId(id)),
  setVoteList: list => dispatch(setVoteList(list))
});

export default connect(
  null,
  mapDispatchToProps
)(BracketUI);
