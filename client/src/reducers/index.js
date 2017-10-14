import { loadUserProfile } from '../common';
import { TOGGLE_SIDEBAR, SET_PLAYER_URI, SET_USER_PROFILE } from '../actions/actionTypes';

const initialState = {
  sidebarIsVisible: true,
  playerURI: '',
  userProfile: loadUserProfile()
};

function rootReducer(state = initialState, action) {
  switch (action.type) {
    case TOGGLE_SIDEBAR:
      return { ...state, sidebarIsVisible: !state.sidebarIsVisible };
    case SET_PLAYER_URI:
      return { ...state, playerURI: action.uri };
    case SET_USER_PROFILE:
      return {...state, userProfile: action.userProfile };
    default:
      return state;
  }
}

export default rootReducer;
