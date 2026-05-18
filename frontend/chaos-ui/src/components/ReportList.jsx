import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Link } from 'react-router-dom';
import { fetchReports } from '../slices/reportsSlice';

const ReportList = () => {
  const dispatch = useDispatch();
  const { items, loading, error } = useSelector((state) => state.reports);

  useEffect(() => {
    dispatch(fetchReports());
  }, [dispatch]);

  return (
    <div className="space-y-6">
      <section className="card">
        <div className="mb-4">
          <h1 className="text-2xl font-semibold text-white">Chaos Reports</h1>
          <p className="text-slate-400">Review AI-generated resilience summaries and recommended fixes.</p>
        </div>

        {loading && <div className="text-sky-300">Loading reports...</div>}
        {error && <div className="rounded-md bg-rose-500/10 p-3 text-rose-200">{error}</div>}

        <div className="overflow-x-auto">
          <table className="min-w-full border-collapse text-left text-sm">
            <thead>
              <tr className="border-b border-slate-800 text-slate-400">
                <th className="px-4 py-3">Experiment ID</th>
                <th className="px-4 py-3">Severity</th>
                <th className="px-4 py-3">Resilience Score</th>
                <th className="px-4 py-3">Generated</th>
                <th className="px-4 py-3">View</th>
              </tr>
            </thead>
            <tbody>
              {items.map((report) => (
                <tr key={report.id} className="border-b border-slate-800 hover:bg-slate-900/80">
                  <td className="px-4 py-3 break-all">{report.experimentId}</td>
                  <td className="px-4 py-3">{report.severity}</td>
                  <td className="px-4 py-3">{report.resilienceScore}</td>
                  <td className="px-4 py-3">{new Date(report.generatedAt).toLocaleString()}</td>
                  <td className="px-4 py-3">
                    <Link
                      to={`/reports/${report.experimentId}`}
                      className="rounded bg-sky-500 px-3 py-1 text-sm text-white hover:bg-sky-400"
                    >
                      Details
                    </Link>
                  </td>
                </tr>
              ))}
              {items.length === 0 && !loading && (
                <tr>
                  <td colSpan="5" className="px-4 py-6 text-center text-slate-400">
                    No reports available yet. Run an experiment and wait for the AI worker to generate a report.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </section>
    </div>
  );
};

export default ReportList;
