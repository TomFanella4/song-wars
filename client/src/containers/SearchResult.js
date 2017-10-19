import React, { Component } from 'react';
import { Card, Button, Image, Icon } from 'semantic-ui-react';
import { connect } from 'react-redux';

import {
  setPlayerURI,
  addRecommendedSong,
  addLoadingSong
} from '../actions';
import { saveRecommendedSongs } from '../common';
import { recommendSong } from '../common/WebServices';

class SearchResult extends Component {

  handleRecommendButtonClick(song) {
    this.props.addLoadingSong(song.id);

    recommendSong(song)
    .then(status => {
      if (status !== 200)
        console.error('Song has already been recommended or another failure occured', status);
      this.props.addRecommendedSong(song.id);
      saveRecommendedSongs(this.props.recommendedSongs);
    })
    .catch(err => console.error(err))
  }

  render() {
    const result = this.props.result;
    return (
      <Card>
        <Card.Content>
          {
            result.album.images[2] && 
            <Image src={result.album.images[2].url} floated='right' size='mini' />
          }
          <Card.Header>
            {result.name + ' '}
            {
              result.popularity <= 50 &&
              <Icon name='diamond' color='red' />
            }
          </Card.Header>
          <Card.Meta>
            {result.artists[0].name}
          </Card.Meta>
        </Card.Content>
        <Card.Content>

          <Button
            icon='play'
            color='green'
            onClick={() => this.props.onPlayButtonClick(result.uri)}
          />

          <Button
            content='Recommend'
            icon='thumbs up'
            labelPosition='left'
            color='yellow'
            disabled={this.props.recommendedSongs[result.id] ? true : false}
            loading={this.props.recommendedSongs[result.id] && this.props.recommendedSongs[result.id].loading}
            onClick={this.handleRecommendButtonClick.bind(this, result)}
          />
          
          {result.popularity}
        </Card.Content>
      </Card>
    )
  }
};

const mapStateToProps = state => ({
  recommendedSongs: state.recommendedSongs
});

const mapDispatchToProps = dispatch => ({
  onPlayButtonClick: uri => dispatch(setPlayerURI(uri)),
  addLoadingSong: id => dispatch(addLoadingSong(id)),
  addRecommendedSong: id => dispatch(addRecommendedSong(id))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(SearchResult);
