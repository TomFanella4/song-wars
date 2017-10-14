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
