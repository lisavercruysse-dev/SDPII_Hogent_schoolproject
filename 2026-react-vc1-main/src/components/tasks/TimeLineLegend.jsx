import { TASK_STATUS_COLORS } from './TimelineConfig.js';

export function TimeLineLegend() {
  return (
    <div className="flex items-center gap-6 mb-4 mt-4 flex-wrap">
      {Object.entries(TASK_STATUS_COLORS).map(([status, colorClass]) => (
        <div key={status} className="flex items-center gap-2">

          <span
            className={`inline-block w-5 h-5 rounded-md border shadow-sm ${colorClass}`}
          />
          <span className="text-sm font-medium text-gray-700">{status}</span>
        </div>
      ))}
    </div>
  );
}