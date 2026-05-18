import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import api from '../services/api';

const initialState = {
  items: [],
  loading: false,
  error: null
};

export const fetchExperiments = createAsyncThunk('experiments/fetch', async () => {
  const response = await api.get('/experiments');
  return response.data;
});

export const createExperiment = createAsyncThunk('experiments/create', async (payload) => {
  const response = await api.post('/experiments', payload);
  return response.data;
});

export const startExperiment = createAsyncThunk('experiments/start', async (experimentId) => {
  const response = await api.post(`/experiments/${experimentId}/start`);
  return response.data;
});

export const stopExperiment = createAsyncThunk('experiments/stop', async (experimentId) => {
  const response = await api.post(`/experiments/${experimentId}/stop`);
  return response.data;
});

export const deleteExperiment = createAsyncThunk('experiments/delete', async (experimentId) => {
  await api.delete(`/experiments/${experimentId}`);
  return experimentId;
});

const experimentsSlice = createSlice({
  name: 'experiments',
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchExperiments.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchExperiments.fulfilled, (state, action) => {
        state.loading = false;
        state.items = action.payload;
      })
      .addCase(fetchExperiments.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      })
      .addCase(createExperiment.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createExperiment.fulfilled, (state, action) => {
        state.loading = false;
        state.items.unshift(action.payload);
      })
      .addCase(createExperiment.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      })
      .addCase(startExperiment.fulfilled, (state, action) => {
        const index = state.items.findIndex((item) => item.id === action.payload.id);
        if (index !== -1) {
          state.items[index] = action.payload;
        }
      })
      .addCase(stopExperiment.fulfilled, (state, action) => {
        const index = state.items.findIndex((item) => item.id === action.payload.id);
        if (index !== -1) {
          state.items[index] = action.payload;
        }
      })
      .addCase(deleteExperiment.fulfilled, (state, action) => {
        state.items = state.items.filter((item) => item.id !== action.payload);
      });
  }
});

export default experimentsSlice.reducer;
