import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Segment, Button, Transition, Divider, Header } from 'semantic-ui-react';

import VoteOption from './VoteOption';
import { setPlayerURI } from '../actions';

class Vote extends Component {
  songs = {
    tracks: [
      {
        s1: {
          name: 'Mischief Managed!',
          artist: 'John Williams',
          album: 'https://i.scdn.co/image/719d1759d0045a390c8e518adc2880fdd09807d3',
          uri: 'spotify:track:3NrAMPb3hlWCmw9kepqBmM'
        },
        s2: {
          name: 'Piano Man',
          artist: 'Billy Joel',
          album: 'https://i.scdn.co/image/7e28eca3ea5124d24690d8093c76eadc069f5b09',
          uri: 'spotify:track:3FCto7hnn1shUyZL42YgfO'
        }
      },
      {
        s1: {
          name: 'Scarborough Fair',
          artist: 'Adagio Music',
          album: 'https://i.scdn.co/image/aecee9528ed18abed2cc184c1f617645e0364e0a',
          uri: 'spotify:track:6ppYBr3v0ee9v1VvZWSne1'
        },
        s2: {
          name: 'Feels',
          artist: 'Calvin Harris',
          album: 'https://i.scdn.co/image/1166dcd37a4afe47d013fb46065556194fee81c3',
          uri: 'spotify:track:5bcTCxgc7xVfSaMV3RuVke'
        }
      }
    ]
  };
  
  state = {
    voteVisible: true,
    index: 0,
    song1: {
      data: this.songs.tracks.length > 0 ? this.songs.tracks[0].s1 : null,
      visible: true
    },
    song2: {
      data: this.songs.tracks.length > 0 ? this.songs.tracks[0].s2 : null,
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

  onVoteHide() {
    const { index } = this.state;
    const newIndex = index + 1;

    newIndex < this.songs.tracks.length ?
      this.setState({
        voteVisible: true,
        index: newIndex,
        song1: {
          data: this.songs.tracks[newIndex].s1,
          visible: true
        },
        song2: {
          data: this.songs.tracks[newIndex].s2,
          visible: true
        }
      })
    :
      this.setState({
        ...this.state,
        index: newIndex
      });
  }

  render() {
    const { voteVisible, song1, song2, index } = this.state;

    return (
      index < this.songs.tracks.length ?
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
                      content={song1.data.name}
                      onClick={() => this.setState({ song1: {...song1, visible: !song1.visible} })}
                    />
                    <Button.Or />
                    <Button
                      color='green'
                      content={song2.data.name}
                      onClick={() => this.setState({ song2: {...song2, visible: !song2.visible} })}
                    />
                  </Button.Group>
                </Segment>
              </div>
            </Transition.Group>
          </Transition>

          <Divider />

          <Button
            content='Skip'
            icon='right arrow'
            labelPosition='right'
            onClick={this.handleNextClick.bind(this)}
            floated='right'
          />
        </Segment>
      :
        <Header as='h1' color='grey' content={'You\'ve voted on all the songs for today!'} />
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
