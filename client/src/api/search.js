import {
  expiredMessage,
  loadUserProfile
} from '../common'

import {
  refreshAuthServer
} from './auth';

export const searchSpotify = query => {
  return new Promise((resolve, reject) => {
    const { access_token } = loadUserProfile();

    if (!access_token) {
      reject('No access_token found');
      return;
    }

    let myHeaders = new Headers();
    myHeaders.append('Authorization', 'Bearer ' + access_token);
    
    let myInit = { method: 'GET',
                    headers: myHeaders,
                    mode: 'cors',
                    cache: 'default' };

    let uri = 'https://api.spotify.com/v1/search';
    uri += '?q=' + encodeURIComponent(query);
    uri += '&type=track';

    fetch(uri, myInit)
    .then(data => data.json())
    .then(json => {
      
      if (json.error) {
        if (json.error.message === expiredMessage) {

          return refreshAuthServer()
          .then(() => searchSpotify(query))
          .then(items => resolve(items))
          .catch(err => reject('Failed to refresh access token: ' + err))

        } else {
          reject(json.error.message);
          return;
        }
      }

      if (json.tracks.items) {
        resolve(json.tracks.items);
      } else {
        reject('Invalid search results');
      }
    })
    .catch(err => reject(err));
  });
}
