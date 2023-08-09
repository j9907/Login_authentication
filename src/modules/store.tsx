import { configureStore } from '@reduxjs/toolkit';
import authReducer from './Loginutil';
import userReducer from './typetest'

const store = configureStore({
  reducer: {
    auth: authReducer,
    user:userReducer,
  },
});

export type auth = typeof store.dispatch
export default store;
