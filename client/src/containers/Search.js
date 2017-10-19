import React, { Component } from 'react';
import { Input, Card, Button, Image } from 'semantic-ui-react';
import { connect } from 'react-redux';

import {
  setPlayerURI,
  addRecommendedSong,
  addLoadingSong
} from '../actions';
import { saveRecommendedSongs } from '../common';
import { searchSpotify, recommendSong } from '../common/WebServices';

class Search extends Component {
  state = { searchResults: [], selectedURI: '' }

  handleSearchChange(event, data) {
    const searchQuery = data.value;
    
    if (searchQuery === '') {
      this.setState({
        searchResults: []
      })
      return;
    }

    searchSpotify(searchQuery)
    .then(results => this.setState({ searchResults: results }))
    .catch(err => console.log(err));
  }

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
    // console.log(this.props.location, window.location);
    // console.log(this.state.searchResults);
    let searchResults = this.state.searchResults.slice();
    // searchResults.splice(5);
    searchResults = searchResults.map(result => (
      <Card key={result.id}>
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
    ));

    return (
      <div>
        <Input size='massive' icon='search' placeholder='Search...' onChange={this.handleSearchChange.bind(this)} />
        {searchResults}
      </div>
    )
  }
}

const mapStateToProps = state => ({
  recommendedSongs: state.recommendedSongs
})

const mapDispatchToProps = dispatch => ({
  onPlayButtonClick: uri => dispatch(setPlayerURI(uri)),
  addLoadingSong: id => dispatch(addLoadingSong(id)),
  addRecommendedSong: id => dispatch(addRecommendedSong(id))
})

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Search);
