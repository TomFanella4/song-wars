import React, { Component } from 'react'
import { Link } from 'react-router-dom';
import { connect } from 'react-redux';
import { Sidebar, Menu, Icon } from 'semantic-ui-react'

class SidebarMenu extends Component {
  render() {
    return (
      <Sidebar as={Menu} animation='push' width='thin' visible={this.props.sidebarIsVisible} icon='labeled' vertical inverted>
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
      </Sidebar>
    )
  }
}

const mapStateToProps = state => ({ sidebarIsVisible: state.sidebarIsVisible })

export default connect(
  mapStateToProps
)(SidebarMenu)