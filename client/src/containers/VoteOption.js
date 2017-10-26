import React from 'react';
import { connect } from 'react-redux';
import { Card, Button, Image } from 'semantic-ui-react';

import { setPlayerURI } from '../actions';

const VoteOption = ({ song, onPlayButtonClick }) => (
  <Card>
    <Card.Content>
      {
        song.album_image && 
        <Image src={song.album_image} floated='right' size='mini' />
      }
      <Card.Header textAlign='left'>
        {song.name}
      </Card.Header>
      <Card.Meta textAlign='left'>
        {song.artist}
      </Card.Meta>
    </Card.Content>
    <Card.Content>

      <Button
        content='Play'
        icon='play'
        labelPosition='left'
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
