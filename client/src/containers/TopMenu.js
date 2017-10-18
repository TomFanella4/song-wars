import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Menu, Button, Header, Icon } from 'semantic-ui-react';

import { toggleSidebar, setUserProfile } from '../actions';
import { deleteUserProfile } from '../common';
import { authSpotify } from '../common/WebServices';

class TopMenu extends Component {

  logout () {
    deleteUserProfile();
    this.props.onLogoutButtonClick();
  }

  render() {
    return (
      <Menu inverted borderless fixed='top' color='green'>
        <Menu.Item icon={true} onClick={this.props.onToggleSidebarClick} >
          <Icon name='content' color='black'/>
        </Menu.Item>
        <Menu.Item>
          <Header content='Song Wars'/>
        </Menu.Item>

          {!this.props.userProfile.access_token ?
            <Menu.Menu position='right'>
              <Menu.Item>
                <Button onClick={authSpotify} secondary>
                  <Header as='h4' inverted>
                    <Icon name='spotify' />
                    <Header.Content>
                      Log In With Spotify
                    </Header.Content>
                  </Header>
                </Button>
              </Menu.Item>
            </Menu.Menu>
          :
            <Menu.Menu position='right'>
              <Menu.Item>
                <Header as='h4'>Welcome, {this.props.userProfile.name}</Header>
              </Menu.Item>
              <Menu.Item>
                <Button onClick={this.logout.bind(this)} secondary>
                  <Header as='h4' inverted>
                      Log Out
                  </Header>
                </Button>
              </Menu.Item>
            </Menu.Menu>
          }
      </Menu>
    )
  }
}

const mapStateToProps = state => ({ userProfile: state.userProfile });

const mapDispatchToProps = dispatch => ({
  onToggleSidebarClick: () => dispatch(toggleSidebar()),
  onLogoutButtonClick: () => dispatch(setUserProfile({}))
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(TopMenu);
