import { configureStore } from '@reduxjs/toolkit';
import experimentsReducer from '../slices/experimentsSlice';
import reportsReducer from '../slices/reportsSlice';

export default configureStore({
  reducer: {
    experiments: experimentsReducer,
    reports: reportsReducer
  }
});
