import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Route, Switch } from 'react-router-dom';
import { Sidebar, Segment, Container } from 'semantic-ui-react'

import '../styles/App.css';
import { changePageWidth } from '../actions'
import TopMenu from '../containers/TopMenu';
import Player from '../containers/Player';
import SidebarMenu from '../containers/SidebarMenu';
import Bracket from './Bracket';
import Search from './Search';
import Statistics from './Statistics';
import About from './About';
import NoMatch from './NoMatch';

class App extends Component {
  sidebarWidth = 150;

  componentDidMount() {
    window.addEventListener("resize", () => this.props.changePageWidth(window.innerWidth));
  }

  componentWillUnmount() {
    window.removeEventListener("resize", () => this.props.changePageWidth(window.innerWidth));
  }

  render() {
    const { pageWidth, sidebarIsVisible } = this.props;
    const mainStyle = {
      width: pageWidth
    };
    
    if (sidebarIsVisible)
      mainStyle.width -= this.sidebarWidth

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
  sidebarIsVisible: state.sidebarIsVisible
});

const mapDispatchToProps = dispatch => ({
  changePageWidth: width => dispatch(changePageWidth(width))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(App);
