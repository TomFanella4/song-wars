import {
  serverURI,
  loadUserProfile
} from '../common';

export const getStats = () => {
  return new Promise((resolve, reject) => {
    const { access_token, user_id } = loadUserProfile();

    if (!access_token || !user_id) {
      reject('No user profile found');
      return;
    }

    let uri = serverURI + '/stats';
    uri += '?user_id=' + encodeURIComponent(user_id);
    uri += '&access_token=' + encodeURIComponent(access_token);

    fetch(uri)
    .then(data => data.json())
    .then(json => resolve(json))
    .catch(err => reject(err));
  });
}
