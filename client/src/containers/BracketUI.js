import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Header, Button, Card, Loader } from 'semantic-ui-react';
import $ from 'jquery';

import { popularCutoff } from '../common';
import { getCurrentBracket, getCurrentVotes, authSpotify, getBracketHistoryFromID } from '../api';
import { setBracketId, setVoteList } from '../actions';
import VoteOption from './VoteOption';

window.jQuery = $;

require('../../node_modules/jquery-bracket/dist/jquery.bracket.min.css');
require('../../node_modules/jquery-bracket/dist/jquery.bracket.min.js');

class BracketUI extends Component {
  state = { preview1: null, preview2: null, winner: null, loading: { bracket: true, vote: true }, round: null }

  onMatchHover(cell) {
    cell && this.setState({ preview1: cell.Option1, preview2: cell.Option2 })
  }

  setupBracket(bracket) {
    const { voteList } = this.props;
    
    // console.log(bracket)
    
    const leftBracket = {
      teams: bracket.RightSide[0].map(cell => [cell.Option1.name, cell.Option2.name]),
      results: bracket.RightSide.map((col, i) => (
        col.map(cell => [
          i + 1 < bracket.RightSide.length || bracket.Finals || !voteList.length ?
            cell.Option1.votes
          :
            null,
          i + 1 < bracket.RightSide.length || bracket.Finals || !voteList.length ?
            cell.Option2.votes
          :
            null,
            i < bracket.RightSide.length - 1 ? cell : null
        ])
      ))
    };

    const rightBracket = {
      teams: bracket.LeftSide[0].map(cell => [cell.Option1.name, cell.Option2.name]),
      results: bracket.LeftSide.map((col, i) => (
        col.map(cell => [
          i + 1 < bracket.LeftSide.length || bracket.Finals || !voteList.length ?
            cell.Option1.votes
          :
            null,
          i + 1 < bracket.LeftSide.length || bracket.Finals || !voteList.length ?
            cell.Option2.votes
          :
            null,
            i < bracket.LeftSide.length - 1 ? cell : null
        ])
      ))
    };

    if (bracket.Winner) {
      this.setState({ winner: bracket.Winner });
    }
    
    this.setState({ 
      loading: {
        ...this.state.loading,
        bracket: false
      },
      round: bracket.round
    });
    
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
  }

  componentDidMount() {
    const { setBracketId, setVoteList, match } = this.props;

    if (match) {

      getBracketHistoryFromID(match.params.bracket_id)
      .then(bracket => this.setupBracket(bracket))
      .catch(err => console.error(err));

    } else {

      getCurrentBracket()
      .then(bracket => {
        this.setupBracket(bracket);

        setBracketId(bracket.bracket_id);
        
        getCurrentVotes(bracket.bracket_id)
        .then(list => list.matchups ? setVoteList(list) : setVoteList({ matchups: [] }))
        .catch(err => setVoteList({ matchups: [] }))
        .then(() => (
          this.setState({
            loading: {
              ...this.state.loading,
              vote: false
            }
          })
        ))
      })
      .catch(err => console.error(err));
    }
  }

  render() {
    const { preview1, preview2, winner, loading, round } = this.state;

    return (
      loading.bracket ?
        <Loader active={true} size='massive' style={{ marginTop: 200 }} />
      :
        <div>
          <div style={{ display: 'flex' }}>
            <div>
              <Header as='h1' content='Hidden Gems' icon='diamond' color='red' />
              <span className="leftBracket" />
            </div>
            <span style={{ width: 175, lineHeight: 6 }}>
              <Header content='VS' style={{ fontSize: 75 }} />
              {
                winner ?
                  <Header
                    as='h1'
                    content={'Winner:\n' + winner.name}
                    color={winner.popularity < popularCutoff ? 'red' : 'green'}
                  />
                :
                  <div>
                    {
                      this.props.userProfile.access_token && !this.props.match ?
                        <Button
                          content='Vote'
                          color='green'
                          size='huge'
                          onClick={this.props.changeView}
                          disabled={loading.vote || !this.props.voteList.matchups.length}
                          loading={loading.vote}
                        />
                      :
                        <Button
                          content='Login to Vote!'
                          color='green'
                          size='large'
                          onClick={authSpotify}
                        />
                    }
                    <Header as='h2' content={'Day ' + (round - 1)} />
                  </div>
              }
            </span>
            <div style={{ marginLeft: 40 }}>
              <Header as='h1' content='Popular' color='green' />
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

const mapStateToProps = state => ({
  userProfile: state.userProfile,
  voteList: state.voteList
});

const mapDispatchToProps = dispatch => ({
  setBracketId: id => dispatch(setBracketId(id)),
  setVoteList: list => dispatch(setVoteList(list))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(BracketUI);
