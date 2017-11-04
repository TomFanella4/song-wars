import {
  serverURI,
  loadUserProfile
} from '../common';

export const getCurrentVotes = bracketId => {
  return new Promise((resolve, reject) => {
    const { access_token, user_id } = loadUserProfile();
    
    if (!access_token || !user_id) {
      reject('No user profile found');
      return;
    }

    let uri = serverURI + '/brackets/current/' + bracketId + '/vote';
    uri += '?user_id=' + encodeURIComponent(user_id);
    uri += '&access_token=' + encodeURIComponent(access_token);

    fetch(uri)
    .then(data => data.json())
    .then(json => resolve(json))
    .catch(err => reject(err));
  });
}

export const recordVote = vote => {
  return new Promise((resolve, reject) => {
    let { user_id, access_token } = loadUserProfile();
    
    if (!access_token || !user_id) {
      reject('No user profile found');
      return;
    }

    let uri = serverURI + '/brackets/current/' + vote.bracket_id + '/vote';

    let body = {
      user_id,
      access_token,
      vote
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
