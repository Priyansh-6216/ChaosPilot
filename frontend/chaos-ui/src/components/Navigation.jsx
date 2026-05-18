import { NavLink } from 'react-router-dom';

const Navigation = () => {
  return (
    <header className="border-b border-slate-800 bg-slate-900/95 backdrop-blur sticky top-0 z-30">
      <div className="max-w-6xl mx-auto flex items-center justify-between px-4 py-4">
        <div>
          <span className="text-xl font-semibold text-sky-400">ChaosPilot</span>
          <p className="text-sm text-slate-400">AI-driven chaos engineering dashboard</p>
        </div>
        <nav className="flex gap-4">
          <NavLink
            to="/experiments"
            className={({ isActive }) =>
              isActive ? 'text-white border-b-2 border-sky-400 pb-1' : 'text-slate-400 hover:text-white'
            }
          >
            Experiments
          </NavLink>
          <NavLink
            to="/reports"
            className={({ isActive }) =>
              isActive ? 'text-white border-b-2 border-sky-400 pb-1' : 'text-slate-400 hover:text-white'
            }
          >
            Reports
          </NavLink>
        </nav>
      </div>
    </header>
  );
};

export default Navigation;
