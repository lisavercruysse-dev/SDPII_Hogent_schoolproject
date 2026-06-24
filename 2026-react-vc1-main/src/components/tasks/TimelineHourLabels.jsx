import { TIMELINE_START, TOTAL_HOURS } from './TimelineConfig.js';

export function TimelineHourLabels() {

  return (
    <div className="flex border-b border-gray-200">
      {/* Member column */}
      <div className="w-40 shrink-0" />
      {/* Hour labels */}
      {Array.from({ length: TOTAL_HOURS + 1 }, (_, i) => (
        <div
          key={i}
          className="flex-1 text-center py-2 text-xs text-gray-400 border-l border-gray-100"
        >
          {String(TIMELINE_START + i).padStart(2, "0")}:00
        </div>
      ))}
    </div>
  );
}