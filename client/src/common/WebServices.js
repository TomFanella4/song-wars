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
    let access_token = loadUserProfile().access_token;
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
        reject(json.error.message);
        return;
      }

      if (json.tracks.items) {
        resolve(json.tracks.items);
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
        song_id: song.id,
        preview_url: song.uri,
        popularity: song.popularity,
        album: {
          album_name: song.album.name
        },
        artists: {
          artists_name: song.artists[0].name
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
