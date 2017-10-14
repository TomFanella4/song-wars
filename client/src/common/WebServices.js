import { loadUserProfile } from '.';

const clientID = '52c0782611f74c95b5bd557ebfc62fcf';
const redirectURI = 'http://localhost:3000/auth';
const serverURI = 'https://zhryq6uuab.execute-api.us-west-2.amazonaws.com/Alpha';

export const authSpotify = () => {
  let url = 'https://accounts.spotify.com/authorize';
  url += '?response_type=code';
  url += '&client_id=' + encodeURIComponent(clientID);
  url += '&redirect_uri=' + encodeURIComponent(redirectURI);
  window.location = url;
}

export const authServer = code => {
  return new Promise((resolve, reject) => {
    if (loadUserProfile().access_token) {
      reject('Already Signed in');
      return;
    }
    let uri = serverURI + '/user/validate';
    uri += '?code=' + encodeURIComponent(code);
  
    fetch(uri)
    .then(data => data.json())
    .then(json => {
      resolve(json);
    })
    .catch(err => reject(err));
  });
}

export const searchSpotify = query => {
  return new Promise((resolve, reject) => {
    var access_token = loadUserProfile().access_token;
    if (!access_token) {
      reject('No access_token found');
      return;
    }
    var myHeaders = new Headers();
    myHeaders.append('Authorization', 'Bearer ' + access_token);
    
    var myInit = { method: 'GET',
                    headers: myHeaders,
                    mode: 'cors',
                    cache: 'default' };

    let uri = 'https://api.spotify.com/v1/search';
    uri += '?q=' + encodeURIComponent(query);
    uri += '&type=track';

    fetch(uri, myInit)
    .then(data => data.json())
    .then(json => {
      if (json.tracks.items) {
        resolve(json.tracks.items);
      }
    })
    .catch(err => reject(err));
  });
}