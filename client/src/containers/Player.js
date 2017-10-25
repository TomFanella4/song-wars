import React from 'react';
import { connect } from 'react-redux';
import { Container, Loader } from 'semantic-ui-react'

const Player = ({ playerURI, pageWidth }) => (
  <Container
    fluid
    style={{ position: 'fixed', bottom: 0, height: 80, backgroundColor: '#282828' }}
  >
    {
      playerURI ?
        <iframe
          title='player'
          src={`https://open.spotify.com/embed?uri=${playerURI}`}
          width={pageWidth}
          height="80"
          frameBorder="0"
          allowTransparency="true"
          style={{position: 'fixed', bottom: 0}}
        />
      :
        <Loader active inverted />
    }
    </Container>
);

const mapStateToProps = state => ({
  playerURI: state.playerURI,
  pageWidth: state.pageWidth
});

export default connect(
  mapStateToProps
)(Player);
