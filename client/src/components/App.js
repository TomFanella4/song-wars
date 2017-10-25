import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Route, Switch } from 'react-router-dom';
import { Sidebar, Segment, Container } from 'semantic-ui-react'

import '../styles/App.css';
import { sidebarWidth, defaultSong } from '../common';
import { getCurrentSong } from '../api'
import { changePageWidth, setPlayerURI } from '../actions'
import TopMenu from '../containers/TopMenu';
import Player from '../containers/Player';
import SidebarMenu from '../containers/SidebarMenu';
import Bracket from './Bracket';
import Search from './Search';
import Statistics from './Statistics';
import About from './About';
import NoMatch from './NoMatch';

class App extends Component {

  componentDidMount() {
    const { userProfile, changePageWidth, onCurrentSongRetrieved } = this.props;

    window.addEventListener("resize", () => changePageWidth(window.innerWidth));

    if (userProfile.access_token) {
      getCurrentSong()
      .then(uri => onCurrentSongRetrieved(uri))
      .catch(err => (
        err.status === 204 ? onCurrentSongRetrieved(defaultSong) : console.error(err))
      );
    }
  }

  componentWillUnmount() {
    window.removeEventListener("resize", () => this.props.changePageWidth(window.innerWidth));
  }

  getMainContentStyle() {
    const { pageWidth, sidebarIsVisible } = this.props;
    
    return {
      width: sidebarIsVisible ? pageWidth - sidebarWidth : pageWidth
    };
  }

  render() {
    const mainStyle = this.getMainContentStyle();

    return (
      <div>
          
        <TopMenu />

        <Container id='sidebar' >
          <Sidebar.Pushable>
            <SidebarMenu />
            <Sidebar.Pusher>
              <Segment basic id='mainContent' style={mainStyle} >
                <Switch>
                  <Route exact path='/' component={Bracket} />
                  <Route path='/bracket' component={Bracket} />
                  <Route path='/search' component={Search} />
                  <Route path='/statistics' component={Statistics} />
                  <Route path='/about' component={About} />
                  <Route component={NoMatch} />
                </Switch>
              </Segment>
            </Sidebar.Pusher>
          </Sidebar.Pushable>
        </Container>

        <Player />

      </div>
    )
  }
}

const mapStateToProps = state => ({
  pageWidth: state.pageWidth,
  sidebarIsVisible: state.sidebarIsVisible,
  userProfile: state.userProfile
});

const mapDispatchToProps = dispatch => ({
  changePageWidth: width => dispatch(changePageWidth(width)),
  onCurrentSongRetrieved: uri => dispatch(setPlayerURI(uri))  
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(App);
