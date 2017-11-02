import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import { Menu, Button, Header, Icon } from 'semantic-ui-react';

import { toggleSidebar, setUserProfile } from '../actions';
import { deleteUserProfile, mobileWidth } from '../common';
import { authSpotify } from '../api';

class TopMenu extends Component {

  logout () {
    deleteUserProfile();
    this.props.onLogoutButtonClick();
  }

  render() {
    return (
      <Menu inverted borderless fixed='top' color='green'>
        <Menu.Item icon={true} onClick={() => console.log('Toggle Sidebar')} >
          <Icon name='content' color='black'/>
        </Menu.Item>
        <Menu.Item>
          <Header content='Song Wars'/>
        </Menu.Item>

        {!this.props.userProfile.access_token ?
          <Menu.Menu position='right'>
            <Menu.Item>
              <Button
                onClick={authSpotify}
                icon='spotify'
                content='Log In With Spotify'
                secondary
              />
            </Menu.Item>
          </Menu.Menu>
        :
          <Menu.Menu position='right'>
            {this.props.pageWidth >= mobileWidth &&
              <Menu.Item>
                <Header as='h4'>Welcome, {this.props.userProfile.user_id}</Header>
              </Menu.Item>
            }
            <Menu.Item>
                <Button
                  onClick={this.logout.bind(this)}
                  icon='log out'
                  content='Log Out'
                  secondary
                />
            </Menu.Item>
          </Menu.Menu>
        }
      </Menu>
    )
  }
}

const mapStateToProps = state => ({
  userProfile: state.userProfile,
  pageWidth: state.pageWidth
});

const mapDispatchToProps = dispatch => ({
  onToggleSidebarClick: () => dispatch(toggleSidebar()),
  onLogoutButtonClick: () => dispatch(setUserProfile({}))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(TopMenu);
