import {
  serverURI,
  loadUserProfile
} from '../common';

export const getBracketHistoryHeaders = () => {
  return new Promise((resolve, reject) => {
    const { access_token, user_id } = loadUserProfile();

    if (!access_token || !user_id) {
      reject('No user profile found');
      return;
    }

    let uri = serverURI + '/brackets/history';
    uri += '?user_id=' + encodeURIComponent(user_id);
    uri += '&access_token=' + encodeURIComponent(access_token);

    fetch(uri)
    .then(data => data.json())
    .then(json => resolve(json))
    .catch(err => reject(err));
  });
}

export const getBracketHistoryFromID = id => {
  return new Promise((resolve, reject) => {
    const { access_token, user_id } = loadUserProfile();

    if (!access_token || !user_id) {
      reject('No user profile found');
      return;
    }

    let uri = serverURI + '/brackets/history/' + id;
    uri += '?user_id=' + encodeURIComponent(user_id);
    uri += '&access_token=' + encodeURIComponent(access_token);

    fetch(uri)
    .then(data => data.json())
    .then(json => resolve(json))
    .catch(err => reject(err));
  });
}
