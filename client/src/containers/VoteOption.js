import React from 'react';
import { connect } from 'react-redux';
import { Card, Button, Image } from 'semantic-ui-react';

import { setPlayerURI } from '../actions';

const VoteOption = ({ song, onPlayButtonClick }) => (
  <Card>
    <Card.Content>
      {
        song.album && 
        <Image src={song.album} floated='right' size='mini' />
      }
      <Card.Header>
        {song.name}
      </Card.Header>
      <Card.Meta>
        {song.artist}
      </Card.Meta>
    </Card.Content>
    <Card.Content>

      <Button
        icon='play'
        color='green'
        onClick={() => onPlayButtonClick(song.uri)}
      />
      
    </Card.Content>
  </Card>
);

const mapDispatchToProps = dispatch => ({
  onPlayButtonClick: uri => dispatch(setPlayerURI(uri))
});

export default connect(
  null,
  mapDispatchToProps
)(VoteOption);
