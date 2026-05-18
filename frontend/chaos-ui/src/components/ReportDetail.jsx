import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useParams, Link } from 'react-router-dom';
import { clearSelected, fetchReportById } from '../slices/reportsSlice';

const ReportDetail = () => {
  const { experimentId } = useParams();
  const dispatch = useDispatch();
  const { selected, loading, error } = useSelector((state) => state.reports);

  useEffect(() => {
    if (experimentId) {
      dispatch(fetchReportById(experimentId));
    }
    return () => dispatch(clearSelected());
  }, [dispatch, experimentId]);

  return (
    <div className="space-y-6">
      <section className="card">
        <div className="mb-4 flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-semibold text-white">Report Details</h1>
            <p className="text-slate-400">Detailed AI report for experiment {experimentId}</p>
          </div>
          <Link to="/reports" className="rounded bg-slate-800 px-4 py-2 text-slate-200 hover:bg-slate-700">
            Back to reports
          </Link>
        </div>

        {loading && <div className="text-sky-300">Loading report...</div>}
        {error && <div className="rounded-md bg-rose-500/10 p-3 text-rose-200">{error}</div>}

        {selected && (
          <div className="space-y-6">
            <div className="rounded-xl border border-slate-800 bg-slate-900 p-5">
              <h2 className="text-xl font-semibold text-white">Summary</h2>
              <p className="mt-3 text-slate-200">{selected.summary}</p>
            </div>

            <div className="grid gap-6 lg:grid-cols-2">
              <div className="rounded-xl border border-slate-800 bg-slate-900 p-5">
                <h3 className="text-lg font-semibold text-white">Root Cause</h3>
                <p className="mt-3 text-slate-200">{selected.rootCause}</p>
              </div>
              <div className="rounded-xl border border-slate-800 bg-slate-900 p-5">
                <h3 className="text-lg font-semibold text-white">Prevention Plan</h3>
                <p className="mt-3 text-slate-200">{selected.preventionPlan}</p>
              </div>
            </div>

            <div className="grid gap-6 lg:grid-cols-3">
              <div className="rounded-xl border border-slate-800 bg-slate-900 p-5">
                <h3 className="text-lg font-semibold text-white">Severity</h3>
                <p className="mt-3 text-slate-200">{selected.severity}</p>
              </div>
              <div className="rounded-xl border border-slate-800 bg-slate-900 p-5">
                <h3 className="text-lg font-semibold text-white">Resilience Score</h3>
                <p className="mt-3 text-slate-200">{selected.resilienceScore}</p>
              </div>
              <div className="rounded-xl border border-slate-800 bg-slate-900 p-5">
                <h3 className="text-lg font-semibold text-white">Generated At</h3>
                <p className="mt-3 text-slate-200">{new Date(selected.generatedAt).toLocaleString()}</p>
              </div>
            </div>

            <div className="rounded-xl border border-slate-800 bg-slate-900 p-5">
              <h3 className="text-lg font-semibold text-white">Recommended Fixes</h3>
              <ul className="mt-3 list-disc space-y-2 pl-5 text-slate-200">
                {selected.recommendedFixes?.map((fix, index) => (
                  <li key={index}>{fix}</li>
                ))}
              </ul>
            </div>

            <div className="rounded-xl border border-slate-800 bg-slate-900 p-5">
              <h3 className="text-lg font-semibold text-white">Blast Radius</h3>
              <pre className="mt-3 overflow-x-auto rounded bg-slate-950 p-4 text-sm text-slate-200">
                {JSON.stringify(selected.blastRadius, null, 2)}
              </pre>
            </div>
          </div>
        )}
      </section>
    </div>
  );
};

export default ReportDetail;
