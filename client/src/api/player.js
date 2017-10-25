import {
  expiredMessage,
  loadUserProfile
} from '../common'

import {
  refreshAuthServer
} from './auth';

export const getCurrentSong = () => {
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

    let uri = 'https://api.spotify.com/v1/me/player/currently-playing';

    fetch(uri, myInit)
    .then(data => data.status === 200 ? data.json() : {})
    .then(json => {
      
      if (json.error) {
        if (json.error.message === expiredMessage) {

          return refreshAuthServer()
          .then(() => getCurrentSong())
          .then(items => resolve(items))
          .catch(err => reject('Failed to refresh access token: ' + err))

        } else {
          reject(json.error.message);
          return;
        }
      }

      if (json.item) {
        resolve(json.item.uri);
      } else {
        reject({ status: 204, message: 'No song found' });
      }
    })
    .catch(err => reject(err));
  });
}
