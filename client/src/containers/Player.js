import React from 'react';
import { connect } from 'react-redux';

const Player = ({ playerURI }) => (
  <iframe
    title='player'
    src={
      playerURI ?
        `https://open.spotify.com/embed?uri=${playerURI}`
      :
        'https://open.spotify.com/embed?uri=spotify:track:78WVLOP9pN0G3gRLFy1rAa'
    }
    width={window.innerWidth}
    height="80"
    frameBorder="0"
    allowTransparency="true"
    style={{position: 'fixed', bottom: 0}}
  />
);

const mapStateToProps = state => ({
  playerURI: state.playerURI
});

export default connect(
  mapStateToProps
)(Player);
