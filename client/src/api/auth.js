import {
  clientID,
  redirectURI,
  scopes,
  serverURI,
  loadUserProfile,
  saveUserProfile,
  deleteUserProfile
} from '../common';

export const authSpotify = () => {
  let url = 'https://accounts.spotify.com/authorize';
  url += '?response_type=code';
  url += '&client_id=' + encodeURIComponent(clientID);
  url += '&redirect_uri=' + encodeURIComponent(redirectURI);
  url += '&scope=' + encodeURIComponent(scopes);
  window.location = url;
}

export const authServer = code => {
  return new Promise((resolve, reject) => {
    const { access_token } = loadUserProfile();

    if (access_token) {
      reject('Already Signed in');
      return;
    }

    let uri = serverURI + '/user/validate';
    uri += '?code=' + encodeURIComponent(code);
  
    fetch(uri)
    .then(data => data.json())
    .then(json => resolve(json))
    .catch(err => reject(err));
  });
}

export const refreshAuthServer = () => {
  return new Promise((resolve, reject) => {
    const { user_id, access_token } = loadUserProfile();
    
    if (!access_token) {
      reject('User not signed in');
      return;
    }

    const uri = serverURI + '/user/validate/update';
    const body = {
      user_id,
      access_token
    }

    const myHeaders = new Headers();
    myHeaders.append('Content-Type', 'application/json');

    const myInit = {
      method: 'POST',
      body: JSON.stringify(body),
      headers: myHeaders
    }
    
    fetch(uri, myInit)
    .then(data => data.json())
    .then(json => {
      if (json.access_token) {
        saveUserProfile(json);
        resolve();
      } else {
        deleteUserProfile();
        authSpotify();
        reject();
      }
    })
    .catch(err => reject(err));
  })
}
