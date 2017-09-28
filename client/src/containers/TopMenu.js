import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Menu, Segment } from 'semantic-ui-react';
import { toggleSidebar } from '../actions'

class TopMenu extends Component {
  render() {
    return (
      <Menu as={Segment} inverted compact fluid basic color='green'>
        <Menu.Item icon='content' onClick={this.props.onToggleSidebarClick} />
        <Menu.Item header>Song Wars</Menu.Item>
      </Menu>
    )
  }
}

const mapDispatchToProps = dispatch => ({ onToggleSidebarClick: () => dispatch(toggleSidebar()) })

export default connect(
  null,
  mapDispatchToProps
)(TopMenu);
