import React, { Component } from 'react';
import { Button, Header } from 'semantic-ui-react';
import querystring from 'querystring';

// import Bracket from './Bracket';

class App extends Component {

  handleLoginClick() {
    // let qs = querystring.stringify({
    //   client_id: '52c0782611f74c95b5bd557ebfc62fcf',
    //   response_type: 'token',
    //   redirect_uri: 'http://localhost:8888/callback'
    // })
    // console.log(qs);
    // fetch(`https://accounts.spotify.com/authorize?${qs}`)
    // .then(data => data.json())
    // .then(json => {
    //   console.log('Success');
    //   console.log(json);
    // })
    // .catch(err => console.log(`error: ${err}`))
    let url = `https://accounts.spotify.com/authorize`;
    url += '?response_type=token';
    url += '&client_id=' + encodeURIComponent('52c0782611f74c95b5bd557ebfc62fcf');
    url += '&redirect_uri=' + encodeURIComponent('http://localhost:3000');
    window.location = url;
  }

  getHashParams() {
    var hashParams = {};
    var e, r = /([^&;=]+)=?([^&;]*)/g,
        q = window.location.hash.substring(1);
    while ( e = r.exec(q)) {
       hashParams[e[1]] = decodeURIComponent(e[2]);
    }
    return hashParams;
  }

  render() {
    const hashParams = this.getHashParams();
    console.log(hashParams);
    return (
      <div>
        <Header as='h1' content='Song Wars' />
        {!hashParams.access_token &&
          <Button onClick={this.handleLoginClick}>
            Login with Spotify
          </Button>
        }
        {/* <Bracket /> */}
      </div>
    );
  }
}

export default App;
