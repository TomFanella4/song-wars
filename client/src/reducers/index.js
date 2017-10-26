import {
  loadUserProfile,
  loadRecommendedSongs,
  mobileWidth
} from '../common';

import {
  TOGGLE_SIDEBAR,
  CHANGE_PAGE_WIDTH,
  SET_PLAYER_URI,
  SET_USER_PROFILE,
  ADD_LOADING_SONG,
  SET_BRACKET_ID,
  ADD_RECOMMENDED_SONG,
  SET_VOTE_LIST
} from '../actions/actionTypes';

const initialState = {
  sidebarIsVisible: window.innerWidth >= mobileWidth,
  pageWidth: window.innerWidth,
  playerURI: '',
  userProfile: loadUserProfile(),
  bracketId: '',
  recommendedSongs: loadRecommendedSongs() || {},
  voteList: []
};

function rootReducer(state = initialState, action) {
  switch (action.type) {
    case TOGGLE_SIDEBAR:
      return { ...state, sidebarIsVisible: !state.sidebarIsVisible };

    case CHANGE_PAGE_WIDTH:
      return {
        ...state,
        pageWidth: action.width,
        sidebarIsVisible: action.width >= mobileWidth
      };

    case SET_PLAYER_URI:
      return { ...state, playerURI: action.uri };

    case SET_USER_PROFILE:
      return {...state, userProfile: action.userProfile };

    case ADD_LOADING_SONG:
      return {
        ...state,
        recommendedSongs: {
          ...state.recommendedSongs,
          [action.id] : {
            loading: true
          }
        }
      };

    case SET_BRACKET_ID:
      return { ...state, bracketId: action.id };

    case ADD_RECOMMENDED_SONG:
      return {
        ...state,
        recommendedSongs: {
          ...state.recommendedSongs,
          [action.id] : {
            loading: false,
            recommended: true
          }
        }
      };

    case SET_VOTE_LIST:
      return { ...state, voteList: action.list };

    default:
      return state;
  }
}

export default rootReducer;
