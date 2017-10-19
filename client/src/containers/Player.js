import React, { Component } from 'react';
import { connect } from 'react-redux';

class Player extends Component {
  state = { width: window.innerWidth };

  updateDimensions() {
    this.setState({ width: window.innerWidth });
  }

  componentDidMount() {
    window.addEventListener("resize", this.updateDimensions.bind(this));
  }

  componentWillUnmount() {
    window.removeEventListener("resize", this.updateDimensions.bind(this));
  }

  render() {
    return (
      <iframe
        title='player'
        src={
          this.props.playerURI ?
            `https://open.spotify.com/embed?uri=${this.props.playerURI}`
          :
            'https://open.spotify.com/embed?uri=spotify:track:78WVLOP9pN0G3gRLFy1rAa'
        }
        width={this.state.width}
        height="80"
        frameBorder="0"
        allowTransparency="true"
        style={{position: 'fixed', bottom: 0}}
      />
    );
  }
}

const mapStateToProps = state => ({
  playerURI: state.playerURI
});

export default connect(
  mapStateToProps
)(Player);
