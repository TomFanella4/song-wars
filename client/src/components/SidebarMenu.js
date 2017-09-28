import React, { Component } from 'react'
import { Link } from 'react-router-dom';
import { connect } from 'react-redux';
import { Sidebar, Menu, Icon } from 'semantic-ui-react'

class SidebarMenu extends Component {
  render() {
    return (
      <Sidebar as={Menu} animation='push' width='wide' visible={this.props.sidebarIsVisible} icon='labeled' vertical inverted>
        <Menu.Item name='home'>
          <Link to='/'>
            <Icon name='home' />
            Home
          </Link>
        </Menu.Item>
        <Menu.Item name='search'>
          <Link to='/search'>
            <Icon name='search' />
            Search
          </Link>
        </Menu.Item>
        <Menu.Item>
        <iframe src={`https://open.spotify.com/embed?uri=${this.props.playerURI}`} width="300" height="380" frameBorder="0" allowTransparency="true"></iframe>
        </Menu.Item>
      </Sidebar>
    )
  }
}

const mapStateToProps = state => ({
  sidebarIsVisible: state.sidebarIsVisible,
  playerURI: state.playerURI
})

export default connect(
  mapStateToProps
)(SidebarMenu);