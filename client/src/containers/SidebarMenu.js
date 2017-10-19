import React, { Component } from 'react'
import { Link } from 'react-router-dom';
import { connect } from 'react-redux';
import { Sidebar, Menu, Icon } from 'semantic-ui-react'

class SidebarMenu extends Component {
  render() {
    return (
      <Sidebar as={Menu} animation='push' width='thin' visible={this.props.sidebarIsVisible} icon='labeled' vertical inverted>
        <Menu.Item>
          <Link to='/home'>
            <Icon name='home' size='large' />
          </Link>
        </Menu.Item>
        <Menu.Item>
          <Link to='/search'>
            <Icon name='search' size='large' />
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