import * as auth from './auth';
import * as search from './search';
import * as recommend from './recommend';
import * as player from './player';

export const { authServer, authSpotify, refreshAuthServer } = auth;
export const { searchSpotify } = search;
export const { recommendSong } = recommend;
export const { getCurrentSong } = player;
