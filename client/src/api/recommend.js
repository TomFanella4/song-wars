import {
  serverURI,
  loadUserProfile
} from '../common'

export const recommendSong = song => {
  return new Promise((resolve, reject) => {
    let { user_id, access_token } = loadUserProfile();
    
    if (!access_token || !user_id) {
      reject('No user profile found');
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
