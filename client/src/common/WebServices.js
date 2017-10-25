import { loadUserProfile, saveUserProfile } from '.';

const clientID = '52c0782611f74c95b5bd557ebfc62fcf';
const redirectURI = 'http://localhost:3000/auth';
const serverURI = 'https://zhryq6uuab.execute-api.us-west-2.amazonaws.com/Alpha';
const scopes = 'user-read-currently-playing';
const expiredMessage = 'The access token expired';

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
      saveUserProfile(json);
      resolve();
    })
    .catch(err => reject(err));
  })
}

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

export const recommendSong = song => {
  return new Promise((resolve, reject) => {
    let { user_id, access_token } = loadUserProfile();
    if (!user_id) {
      reject('No user_id found');
      return;
    }

    let uri = serverURI + '/song/recommend';
    let body = {
      user_id,
      access_token,
      song: {
        name: song.name,
        id: song.id,
        preview_url: song.uri,
        popularity: song.popularity,
        album: {
          name: song.album.name,
          image_url: song.album.images[2] ? song.album.images[2].url : null
        },
        artists: {
          name: song.artists[0].name
        }
      }
    };

    let myHeaders = new Headers();
    myHeaders.append('Content-Type', 'application/json');

    let myInit = {
      method: 'POST',
      body: JSON.stringify(body),
      headers: myHeaders
    }

    fetch(uri, myInit)
    .then(data => data.status)
    .then(status => resolve(status))
    .catch(err => reject(err));
  });
}
