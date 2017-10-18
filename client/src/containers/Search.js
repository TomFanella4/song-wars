import React, { Component } from 'react';
import { Input, Card, Button, Image } from 'semantic-ui-react';
import { connect } from 'react-redux';

import { setPlayerURI, addRecommendedSong } from '../actions';
import { saveRecommendedSongs } from '../common';
import { searchSpotify, recommendSong } from '../common/WebServices';

class Search extends Component {
  state = { searchResults: [], selectedURI: '' }

  handleSearchChange (event, data) {
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

  handleRecommendButtonClick (song) {
    recommendSong(song)
    .then(status => {
      console.log(status);
      if (status === 200 || !this.props.recommendedSongs[song.id]) {
        this.props.addRecommendedSong(song.id);
        saveRecommendedSongs(this.props.recommendedSongs);
      }
    })
    .catch(err => console.error(err))
  }

  render() {
    // console.log(this.props.location, window.location);
    // console.log(this.state.searchResults);
    let searchResults = this.state.searchResults.slice();
    searchResults.splice(5);
    searchResults = searchResults.map(result => (
      <Card key={result.id}>
        <Card.Content>
          <Image src={result.album.images[2].url} floated='right' size='mini' />
          <Card.Header>
            {result.name}
          </Card.Header>
          <Card.Meta>
            {result.artists[0].name + ' ' + result.popularity}
          </Card.Meta>
          <Button icon='play' onClick={() => this.props.onPlayButtonClick(result.uri)} />
          <Button
            disabled={this.props.recommendedSongs[result.id]}
            icon='thumbs up'
            onClick={() => this.handleRecommendButtonClick(result)}
          />
        </Card.Content>
      </Card>
    ));

    return (
      <div>
        {/* <iframe src={`https://open.spotify.com/embed?uri=${this.state.selectedURI}`} width="300" height="380" frameBorder="0" allowTransparency="true"></iframe> */}
        {/* <br/> */}
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
  addRecommendedSong: id => new Promise((resolve, reject) => dispatch(addRecommendedSong(id)))
})

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Search);
