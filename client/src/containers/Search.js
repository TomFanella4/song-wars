import React, { Component } from 'react';
import { Input, Card, Button, Icon, Image } from 'semantic-ui-react';
import querystring from 'querystring';
import { setPlayerURI } from '../actions';
import { connect } from 'react-redux';

class Search extends Component {
  state = { searchResults: [], selectedURI: '' }

  getHashParams() {
    var hashParams = {};
    var e, r = /([^&;=]+)=?([^&;]*)/g,
        q = window.location.hash.substring(1);
    while ( e = r.exec(q)) {
       hashParams[e[1]] = decodeURIComponent(e[2]);
    }
    return hashParams;
  }

  handleSearchChange(event, data) {
    
    if (data.value === '') {
      this.setState({
        searchResults: []
      })
      return;
    }

    let token = this.getHashParams().access_token;

    var myHeaders = new Headers();
    myHeaders.append('Authorization', 'Bearer ' + token);
    
    var myInit = { method: 'GET',
                   headers: myHeaders,
                   mode: 'cors',
                   cache: 'default' };

    let qs = querystring.stringify({
      q: data.value,
      type: 'track'
    });

    fetch(`https://api.spotify.com/v1/search?${qs}`, myInit)
    .then(data => data.json())
    .then((json) => {
      if (json.tracks.items) {
        this.setState({
          searchResults: json.tracks.items
        });
      }
    })
    .catch(err => console.error(err))
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
