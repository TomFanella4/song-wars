import {
  serverURI,
  loadUserProfile
} from '../common';

export const getCurrentVotes = bracketId => {
  return new Promise((resolve, reject) => {
    const { access_token, user_id } = loadUserProfile();

    let uri = serverURI + '/brackets/current/' + bracketId + '/vote';
    uri += '?user_id=' + encodeURIComponent(user_id);
    uri += '&access_token=' + encodeURIComponent(access_token);

    fetch(uri)
    .then(data => data.json())
    .then(json => resolve(json))
    .catch(err => reject(err));
  });
}
