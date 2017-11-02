import {
  serverURI
} from '../common';

export const getCurrentBracket = () => {
  return new Promise((resolve, reject) => {
    const uri = serverURI + '/brackets/current'

    fetch(uri)
    .then(data => data.json())
    .then(json => resolve(json))
    .catch(err => reject(err));
  });
}
