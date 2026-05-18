import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import api from '../services/api';

const initialState = {
  items: [],
  selected: null,
  loading: false,
  error: null
};

export const fetchReports = createAsyncThunk('reports/fetch', async () => {
  const response = await api.get('/reports');
  return response.data;
});

export const fetchReportById = createAsyncThunk('reports/fetchById', async (experimentId) => {
  const response = await api.get(`/reports/${experimentId}`);
  return response.data;
});

const reportsSlice = createSlice({
  name: 'reports',
  initialState,
  reducers: {
    clearSelected: (state) => {
      state.selected = null;
      state.error = null;
    }
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchReports.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchReports.fulfilled, (state, action) => {
        state.loading = false;
        state.items = action.payload;
      })
      .addCase(fetchReports.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      })
      .addCase(fetchReportById.pending, (state) => {
        state.loading = true;
        state.selected = null;
        state.error = null;
      })
      .addCase(fetchReportById.fulfilled, (state, action) => {
        state.loading = false;
        state.selected = action.payload;
      })
      .addCase(fetchReportById.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      });
  }
});

export const { clearSelected } = reportsSlice.actions;
export default reportsSlice.reducer;
