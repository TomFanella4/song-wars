import { TOGGLE_SIDEBAR, SET_PLAYER_URI } from '../actions/actionTypes';

const initialState = {
  sidebarIsVisible: true,
  playerURI: ''
};

function rootReducer(state = initialState, action) {
  switch (action.type) {
    case TOGGLE_SIDEBAR:
      return { ...state, sidebarIsVisible: !state.sidebarIsVisible }
    case SET_PLAYER_URI:
      return { ...state, playerURI: action.uri }
    default:
      return state;
  }
}

export default rootReducer;
