import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import {
  createExperiment,
  deleteExperiment,
  fetchExperiments,
  startExperiment,
  stopExperiment
} from '../slices/experimentsSlice';

const failureTypes = [
  'LATENCY',
  'TIMEOUT',
  'SERVICE_CRASH',
  'CPU_SPIKE',
  'MEMORY_PRESSURE',
  'DB_SLOWDOWN',
  'KAFKA_LAG',
  'HTTP_500_ERROR'
];

const targetServices = ['order-service', 'payment-service', 'inventory-service', 'user-service'];

const ExperimentList = () => {
  const dispatch = useDispatch();
  const { items, loading, error } = useSelector((state) => state.experiments);
  const [form, setForm] = useState({
    name: '',
    targetService: 'order-service',
    failureType: 'LATENCY',
    durationSeconds: 30,
    intensity: 50
  });

  useEffect(() => {
    dispatch(fetchExperiments());
  }, [dispatch]);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((state) => ({
      ...state,
      [name]: name === 'durationSeconds' || name === 'intensity' ? Number(value) : value
    }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    dispatch(createExperiment(form));
    setForm((state) => ({ ...state, name: '' }));
  };

  return (
    <div className="space-y-6">
      <section className="card">
        <div className="mb-4">
          <h1 className="text-2xl font-semibold text-white">Experiments</h1>
          <p className="text-slate-400">Create and manage chaos experiments across your microservices.</p>
        </div>

        <form onSubmit={handleSubmit} className="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
          <div>
            <label className="block text-sm font-medium text-slate-300">Experiment Name</label>
            <input
              name="name"
              value={form.name}
              onChange={handleChange}
              className="mt-2 w-full px-3 py-2 bg-slate-950"
              placeholder="API latency spike"
              required
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-slate-300">Target Service</label>
            <select name="targetService" value={form.targetService} onChange={handleChange} className="mt-2 w-full px-3 py-2 bg-slate-950">
              {targetServices.map((service) => (
                <option key={service} value={service}>
                  {service}
                </option>
              ))}
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-slate-300">Failure Type</label>
            <select name="failureType" value={form.failureType} onChange={handleChange} className="mt-2 w-full px-3 py-2 bg-slate-950">
              {failureTypes.map((type) => (
                <option key={type} value={type}>
                  {type}
                </option>
              ))}
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-slate-300">Duration (seconds)</label>
            <input
              name="durationSeconds"
              type="number"
              min="1"
              value={form.durationSeconds}
              onChange={handleChange}
              className="mt-2 w-full px-3 py-2 bg-slate-950"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-slate-300">Intensity (%)</label>
            <input
              name="intensity"
              type="number"
              min="1"
              max="100"
              value={form.intensity}
              onChange={handleChange}
              className="mt-2 w-full px-3 py-2 bg-slate-950"
            />
          </div>
          <div className="flex items-end">
            <button type="submit" className="inline-flex justify-center rounded-md bg-sky-500 px-4 py-2 text-white hover:bg-sky-400">
              Create Experiment
            </button>
          </div>
        </form>
      </section>

      <section className="card">
        <div className="mb-4 flex items-center justify-between">
          <div>
            <h2 className="text-xl font-semibold text-white">Active Experiments</h2>
            <p className="text-slate-400">Review experiment state and trigger lifecycle actions.</p>
          </div>
          {loading && <span className="text-sky-300">Loading...</span>}
        </div>

        {error && <div className="rounded-md bg-rose-500/10 p-3 text-rose-200">{error}</div>}

        <div className="overflow-x-auto">
          <table className="min-w-full border-collapse text-left text-sm">
            <thead>
              <tr className="border-b border-slate-800 text-slate-400">
                <th className="px-4 py-3">Name</th>
                <th className="px-4 py-3">Target</th>
                <th className="px-4 py-3">Failure</th>
                <th className="px-4 py-3">Duration</th>
                <th className="px-4 py-3">Intensity</th>
                <th className="px-4 py-3">Status</th>
                <th className="px-4 py-3">Actions</th>
              </tr>
            </thead>
            <tbody>
              {items.map((experiment) => (
                <tr key={experiment.id} className="border-b border-slate-800 hover:bg-slate-900/80">
                  <td className="px-4 py-3">{experiment.name}</td>
                  <td className="px-4 py-3">{experiment.targetService}</td>
                  <td className="px-4 py-3">{experiment.failureType}</td>
                  <td className="px-4 py-3">{experiment.durationSeconds}s</td>
                  <td className="px-4 py-3">{experiment.intensity}%</td>
                  <td className="px-4 py-3 text-slate-200">{experiment.status}</td>
                  <td className="px-4 py-3 space-x-2">
                    <button
                      type="button"
                      onClick={() => dispatch(startExperiment(experiment.id))}
                      className="rounded bg-emerald-500 px-3 py-1 text-sm text-white hover:bg-emerald-400"
                    >
                      Start
                    </button>
                    <button
                      type="button"
                      onClick={() => dispatch(stopExperiment(experiment.id))}
                      className="rounded bg-amber-500 px-3 py-1 text-sm text-white hover:bg-amber-400"
                    >
                      Stop
                    </button>
                    <button
                      type="button"
                      onClick={() => dispatch(deleteExperiment(experiment.id))}
                      className="rounded bg-rose-500 px-3 py-1 text-sm text-white hover:bg-rose-400"
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
              {items.length === 0 && !loading && (
                <tr>
                  <td colSpan="7" className="px-4 py-6 text-center text-slate-400">
                    No experiments found. Create one to begin chaos testing.
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

export default ExperimentList;
