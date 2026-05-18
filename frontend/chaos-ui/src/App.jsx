import { Routes, Route, Navigate } from 'react-router-dom';
import Navigation from './components/Navigation';
import ExperimentList from './components/ExperimentList';
import ReportList from './components/ReportList';
import ReportDetail from './components/ReportDetail';

function App() {
  return (
    <div className="min-h-screen bg-slate-950 text-slate-100">
      <Navigation />
      <main className="max-w-6xl mx-auto px-4 py-6">
        <Routes>
          <Route path="/" element={<Navigate replace to="/experiments" />} />
          <Route path="/experiments" element={<ExperimentList />} />
          <Route path="/reports" element={<ReportList />} />
          <Route path="/reports/:experimentId" element={<ReportDetail />} />
        </Routes>
      </main>
    </div>
  );
}

export default App;
