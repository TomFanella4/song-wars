import React, { Component } from 'react'
import { Link } from 'react-router-dom';
import { connect } from 'react-redux';
import { Sidebar, Menu, Icon } from 'semantic-ui-react'

class SidebarMenu extends Component {
  render() {
    return (
      <Sidebar as={Menu} animation='push' width='thin' visible={this.props.sidebarIsVisible} icon='labeled' vertical inverted>
        <Menu.Item>
          <Link to='/bracket'>
            <Icon name='trophy' size='large' />
            Bracket
          </Link>
        </Menu.Item>
        <Menu.Item>
          <Link to='/search'>
            <Icon name='search' size='large' />
            Search
          </Link>
        </Menu.Item>
        <Menu.Item>
          <Link to='/statistics'>
            <Icon name='bar chart' size='large' />
            Statistics
          </Link>
        </Menu.Item>
        <Menu.Item>
          <Link to='/about'>
            <Icon name='info' size='large' />
            About
          </Link>
        </Menu.Item>
      </Sidebar>
    )
  }
}

const mapStateToProps = state => ({
  sidebarIsVisible: state.sidebarIsVisible,
})

export default connect(
  mapStateToProps
)(SidebarMenu);