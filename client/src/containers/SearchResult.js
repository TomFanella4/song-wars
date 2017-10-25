import React, { Component } from 'react';
import { Card, Button, Image, Icon, Popup } from 'semantic-ui-react';
import { connect } from 'react-redux';

import {
  setPlayerURI,
  addRecommendedSong,
  addLoadingSong
} from '../actions';
import { saveRecommendedSongs, miniWidth } from '../common';
import { recommendSong } from '../api';

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
    const { result, index, pageWidth } = this.props;
    return (
      <Card>
        <Card.Content>
          {
            result.album.images[2] && 
            <Image src={result.album.images[2].url} floated='right' size='mini' />
          }
          {
            result.popularity <= 50 &&
            <Popup
              trigger={<Icon size='large' name='diamond' color='red' style={{float: 'right'}} />}
              content='Hidden Gem'
              inverted
            />
          }
          <Card.Header>
            {index + '. ' + result.name + ' '}
            
          </Card.Header>
          <Card.Meta>
            {result.artists[0].name + ' \u2022 ' + result.album.name}
          </Card.Meta>
        </Card.Content>
        <Card.Content textAlign='center'>

          <Button
            content={pageWidth >= miniWidth ? 'Play' : null}
            icon='play'
            labelPosition={pageWidth >= miniWidth ? 'left' : null}
            color='green'
            onClick={() => this.props.onPlayButtonClick(result.uri)}
          />

          <Button
            content={pageWidth >= miniWidth ? 'Recommend' : null}
            icon='thumbs up'
            labelPosition={pageWidth >= miniWidth ? 'left' : null}
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
  recommendedSongs: state.recommendedSongs,
  pageWidth: state.pageWidth
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
