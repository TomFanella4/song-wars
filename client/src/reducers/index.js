import {
  loadUserProfile,
  loadRecommendedSongs
} from '../common';

import {
  TOGGLE_SIDEBAR,
  SET_PLAYER_URI,
  SET_USER_PROFILE,
  ADD_RECOMMENDED_SONG
} from '../actions/actionTypes';

const initialState = {
  sidebarIsVisible: true,
  playerURI: '',
  userProfile: loadUserProfile(),
  recommendedSongs: loadRecommendedSongs() || {}
};

function rootReducer(state = initialState, action) {
  switch (action.type) {
    case TOGGLE_SIDEBAR:
      return { ...state, sidebarIsVisible: !state.sidebarIsVisible };

    case SET_PLAYER_URI:
      return { ...state, playerURI: action.uri };

    case SET_USER_PROFILE:
      return {...state, userProfile: action.userProfile };

    case ADD_RECOMMENDED_SONG:
      const newRecommendedSongs = {...state.recommendedSongs};
      newRecommendedSongs[action.id] = true;
      return {...state, recommendedSongs: newRecommendedSongs}

    default:
      return state;
  }
}

export default rootReducer;
