export const mobileWidth = 768;

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
