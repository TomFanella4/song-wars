import { TOGGLE_SIDEBAR, SET_PLAYER_URI, SET_USER_PROFILE } from './actionTypes';

export const toggleSidebar = () => ({ type: TOGGLE_SIDEBAR });
export const setPlayerURI = uri => ({ type: SET_PLAYER_URI, uri });
export const setUserProfile = userProfile => ({ type: SET_USER_PROFILE, userProfile });
