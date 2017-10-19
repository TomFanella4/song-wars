import React, { Component } from 'react';
import { Card, Button, Image } from 'semantic-ui-react';
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
            {result.name}
          </Card.Header>
          <Card.Meta>
            {result.artists[0].name + ' ' + result.popularity}
          </Card.Meta>

          <Button
            icon='play'
            color='green'
            onClick={() => this.props.onPlayButtonClick(result.uri)}
          />

          <Button
            icon='thumbs up'
            color='yellow'
            disabled={this.props.recommendedSongs[result.id] ? true : false}
            loading={this.props.recommendedSongs[result.id] && this.props.recommendedSongs[result.id].loading}
            onClick={this.handleRecommendButtonClick.bind(this, result)}
          />
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
