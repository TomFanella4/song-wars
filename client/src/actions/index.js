import { TOGGLE_SIDEBAR, SET_PLAYER_URI } from './actionTypes';

export const toggleSidebar = () => ({ type: TOGGLE_SIDEBAR })
export const setPlayerURI = uri => ({ type: SET_PLAYER_URI, uri })
