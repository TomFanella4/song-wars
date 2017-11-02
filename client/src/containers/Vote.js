import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Segment, Button, Transition, Divider, Header } from 'semantic-ui-react';

import VoteOption from './VoteOption';
import { setPlayerURI } from '../actions';
import { recordVote } from '../api'

class Vote extends Component {
  // songs = {
  //   tracks: [
  //     {
  //       s1: {
  //         name: 'Mischief Managed!',
  //         artist: 'John Williams',
  //         album_image: 'https://i.scdn.co/image/719d1759d0045a390c8e518adc2880fdd09807d3',
  //         uri: 'spotify:track:3NrAMPb3hlWCmw9kepqBmM'
  //       },
  //       s2: {
  //         name: 'Piano Man',
  //         artist: 'Billy Joel',
  //         album_image: 'https://i.scdn.co/image/7e28eca3ea5124d24690d8093c76eadc069f5b09',
  //         uri: 'spotify:track:3FCto7hnn1shUyZL42YgfO'
  //       }
  //     },
  //     {
  //       s1: {
  //         name: 'Scarborough Fair',
  //         artist: 'Adagio Music',
  //         album_image: 'https://i.scdn.co/image/aecee9528ed18abed2cc184c1f617645e0364e0a',
  //         uri: 'spotify:track:6ppYBr3v0ee9v1VvZWSne1'
  //       },
  //       s2: {
  //         name: 'Feels',
  //         artist: 'Calvin Harris',
  //         album_image: 'https://i.scdn.co/image/1166dcd37a4afe47d013fb46065556194fee81c3',
  //         uri: 'spotify:track:5bcTCxgc7xVfSaMV3RuVke'
  //       }
  //     }
  //   ]
  // };
  
  state = {
    voteVisible: true,
    index: 0,
    song1: {
      data: this.props.voteList.matchups &&
        this.props.voteList.matchups.length > 0 ? this.props.voteList.matchups[0].song1 : null,
      visible: true
    },
    song2: {
      data: this.props.voteList.matchups &&
        this.props.voteList.matchups.length > 0 ? this.props.voteList.matchups[0].song2 : null,
      visible: true
    }
  };
  
  handleNextClick() {
    this.setState({
      voteVisible: false,
      song1: {
        ...this.state.song1,
        visible: true
      },
      song2: {
        ...this.state.song2,
        visible: true
      }
    });
  }

  handleVoteClick(song, index) {
    const songName = 'song' + index;

    this.setState({
      [songName]: {
        ...song,
        visible: !song.visible
      }
    });

    const vote = {
      song_id: song.data.id,
      bracket_id: song.data.bracket_id,
      round: this.props.voteList.round,
      position: song.data.position
    };

    // console.log(vote);

    recordVote(vote)
    .then(res => console.log(res))
    .catch(err => console.error(err));
  }

  onVoteHide() {
    const { index } = this.state;
    const { voteList } = this.props;
    const newIndex = index + 1;

    newIndex < voteList.matchups.length ?
      this.setState({
        voteVisible: true,
        index: newIndex,
        song1: {
          data: voteList.matchups[newIndex].song1,
          visible: true
        },
        song2: {
          data: voteList.matchups[newIndex].song2,
          visible: true
        }
      })
    :
      this.setState({
        ...this.state,
        index: newIndex
      });
  }

  formatVoteName(name) {
    let formatedName = name.slice(0, 20);

    return name.length > 20 ?
      formatedName.concat('...')
    :
      formatedName;
  }

  render() {
    const { voteVisible, song1, song2, index } = this.state;
    const { voteList } = this.props;
    // console.log(voteList);

    return (
      voteList.matchups &&
      index < voteList.matchups.length ?
        <Segment compact>
        
          <Transition
            animation={voteVisible ? 'fly right' : 'fly left'}
            visible={voteVisible}
            onHide={this.onVoteHide.bind(this)}
          >
            <Transition.Group>
              <div>
                <Segment.Group as={Segment} horizontal basic >
                  <Segment basic compact >
                    <Transition 
                      animation='bounce'
                      visible={song1.visible}
                      onHide={this.handleNextClick.bind(this)}
                    >
                      <Transition.Group>
                        <VoteOption song={song1.data} />
                      </Transition.Group>
                    </Transition>
                  </Segment>
                  <Segment basic compact>
                    <Transition
                      animation='bounce'
                      visible={song2.visible}
                      onHide={this.handleNextClick.bind(this)}
                    >
                      <Transition.Group>
                        <VoteOption song={song2.data} />
                      </Transition.Group>
                    </Transition>
                  </Segment>
                </Segment.Group>
                <Segment basic textAlign='center'>
                  <Button.Group size='massive' >
                    <Button
                      color='red'
                      content={this.formatVoteName(song1.data.name)}
                      onClick={this.handleVoteClick.bind(this, song1, 1)}
                    />
                    <Button.Or />
                    <Button
                      color='green'
                      content={this.formatVoteName(song2.data.name)}
                      onClick={this.handleVoteClick.bind(this, song2, 2)}
                    />
                  </Button.Group>
                </Segment>
              </div>
            </Transition.Group>
          </Transition>

          <Divider />

          <Button
            content='Bracket'
            icon='left arrow'
            labelPosition='left'
            onClick={this.props.changeView}
            floated='left'
          />

          <Button
            content='Skip'
            icon='right arrow'
            labelPosition='right'
            onClick={this.handleNextClick.bind(this)}
            floated='right'
          />
        </Segment>
      :
        <div>
          <Header as='h1' color='grey' content={'You\'ve voted on all the songs for today!'} />
          <Button
            content='Bracket'
            onClick={this.props.changeView}
          />
        </div>
    );
  }
}

const mapStateToProps = state => ({
  voteList: state.voteList,
  bracketId: state.bracketId
});

const mapDispatchToProps = dispatch => ({
  setPlayerURI: uri => dispatch(setPlayerURI(uri))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Vote);
