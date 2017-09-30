import { TOGGLE_SIDEBAR } from '../actions/actionTypes';

const initialState = {
  sidebarIsVisible: true
};

function rootReducer(state = initialState, action) {
  switch (action.type) {
    case TOGGLE_SIDEBAR:
      return { ...state, sidebarIsVisible: !state.sidebarIsVisible }
    default:
      return state;
  }
}

export default rootReducer;
