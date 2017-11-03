export const mobileWidth = 850;
export const miniWidth = 363;
export const sidebarWidth = 150;
export const popularCutoff = 50;

export const clientID = '52c0782611f74c95b5bd557ebfc62fcf';
export const redirectURI = 'http://songwars-frontend-bugged.s3-website-us-west-2.amazonaws.com/auth';
export const serverURI = 'https://tluquld4j9.execute-api.us-west-2.amazonaws.com/Release';
export const scopes = 'user-read-currently-playing';
export const expiredMessage = 'The access token expired';
export const defaultSong = 'spotify:track:78WVLOP9pN0G3gRLFy1rAa';

export const saveUserProfile = userProfile => {
  const { access_token, name, user_id } = userProfile;
  
  access_token && localStorage.setItem('access_token', access_token);
  name && localStorage.setItem('name', name);
  user_id && localStorage.setItem('user_id', user_id);
}

export const loadUserProfile = () => (
  {
    access_token: localStorage.getItem('access_token'),
    name: localStorage.getItem('name'),
    user_id: localStorage.getItem('user_id')
  }
);

export const deleteUserProfile = () => {
  localStorage.removeItem('access_token');
  localStorage.removeItem('name');
  localStorage.removeItem('user_id');
}

export const saveRecommendedSongs = songs => {
  localStorage.setItem('recommended_songs', JSON.stringify(songs));
}

export const loadRecommendedSongs = () => (
  JSON.parse(localStorage.getItem('recommended_songs'))
)
