import React, { Component } from 'react';
import { Input, Card, Button, Icon, Image } from 'semantic-ui-react';
import { setPlayerURI } from '../actions';
import { connect } from 'react-redux';
import { searchSpotify } from '../common/WebServices'

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

  render() {
    console.log(this.props.location, window.location);
    console.log(this.state.searchResults);
    let searchResults = this.state.searchResults.slice();
    searchResults.splice(5);
    searchResults = searchResults.map((result) => (
      <Card key={result.id}>
        <Card.Content>
          <Image src={result.album.images[2].url} floated='right' size='mini' />          
          <Card.Header>
            {result.name}
          </Card.Header>
          <Card.Meta>
            {result.artists[0].name}
          </Card.Meta>
          {/* <Button icon='play' onClick={() => this.props.onPlayButtonClick(result.uri)} /> */}
          <Button animated onClick={() => this.props.onPlayButtonClick(result.uri)}>
            <Button.Content visible>
              <Icon name='play' />
            </Button.Content>
            <Button.Content hidden>
              <Icon name='left arrow' />
            </Button.Content>
          </Button>
          <Button icon='thumbs up' />
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

const mapDispatchToProps = dispatch => ({ onPlayButtonClick: uri => dispatch(setPlayerURI(uri)) })

export default connect(
  null,
  mapDispatchToProps
)(Search);
