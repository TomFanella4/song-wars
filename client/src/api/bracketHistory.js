import {
  serverURI,
  loadUserProfile
} from '../common';

export const getbracketHistoryHeaders = () => {
  return new Promise((resolve, reject) => {
    const { access_token, user_id } = loadUserProfile();

    let uri = serverURI + '/brackets/history';
    uri += '?user_id=' + encodeURIComponent(user_id);
    uri += '&access_token=' + encodeURIComponent(access_token);

    fetch(uri)
    .then(data => data.status)
    .then(json => resolve(json))
    .catch(err => reject(err));
  });
}

// getbracketHistoryHeaders()
// .then(res => console.log(res))
// .catch(err => console.error(err))
