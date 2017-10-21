export const mobileWidth = 768;

export const saveUserProfile = userProfile => {
  localStorage.setItem('access_token', userProfile.access_token);
  localStorage.setItem('name', userProfile.name);
  localStorage.setItem('user_id', userProfile.user_id);
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
