// src/components/tasks/PlanningTimeline.jsx
import { useMemo } from "react";
import { TimelineHourLabels } from "./TimelineHourLabels";
import { TOTAL_HOURS } from "./TimelineConfig.js";
import { TaskBlock } from "./TaskBlock.jsx";

function toDateInputValue(date) {
  return date.toISOString().split("T")[0];
}

const getWeekStart = (dateInput) => {
  const date = new Date(dateInput);
  const day = date.getDay();
  const diff = day === 0 ? -6 : 1 - day;
  date.setDate(date.getDate() + diff);
  date.setHours(0, 0, 0, 0);
  return date;
};

export function PlanningTimeline({ tasks, selectedDate }) {
  const { weekTasks, weekRangeLabel } = useMemo(() => {
    const anchor = selectedDate ? new Date(selectedDate) : new Date();
    const weekStart = getWeekStart(anchor);
    const weekDays = Array.from({ length: 7 }, (_, index) => {
      const day = new Date(weekStart);
      day.setDate(weekStart.getDate() + index);
      return day;
    });
    const weekEnd = weekDays[6];
    const weekTasks = weekDays.map((day) => {
      const dayKey = toDateInputValue(day);
      const items = tasks.filter((task) => {
        const taskDate = task.startdatum || task.datum;
        return toDateInputValue(new Date(taskDate)) === dayKey;
      });
      return {
        day,
        dayKey,
        items,
      };
    });

    const formatter = new Intl.DateTimeFormat("nl-BE", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
    });
    const weekRangeLabel = `${formatter.format(weekStart)} - ${formatter.format(weekEnd)}`;

    return { weekTasks, weekRangeLabel };
  }, [tasks, selectedDate]);

  return (
    <div className="bg-white rounded-lg border border-gray-200 overflow-x-hidden mb-6">
      <div className="min-w-200">
        <TimelineHourLabels />
        <div className="px-4 py-2 text-xs font-medium text-gray-500 border-b border-gray-100">
          Weekplanning: {weekRangeLabel}
        </div>

        {weekTasks.map(({ day, dayKey, items }) => (
          <div
            key={dayKey}
            className="flex relative min-h-22 border-b border-gray-100 last:border-b-0"
          >
            <div className="w-40 shrink-0 px-4 py-3 border-r border-gray-200">
              <p className="font-semibold text-sm text-gray-800">
                {day.toLocaleDateString("nl-BE", { weekday: "long" })}
              </p>
              <p className="text-xs text-gray-500 mt-0.5">
                {day.toLocaleDateString("nl-BE")}
              </p>
              <p className="text-xs text-gray-500 mt-0.5">
                Totaal: {items.length}
              </p>
            </div>

            <div className="flex-1 relative my-2">
              {Array.from({ length: TOTAL_HOURS + 1 }, (_, i) => (
                <div
                  key={i}
                  className="absolute top-0 bottom-0 border-l border-gray-100"
                  style={{ left: `${(i / TOTAL_HOURS) * 100}%` }}
                />
              ))}

              {items.length === 0 && (
                <div className="absolute inset-0 flex items-center pl-4 text-sm text-gray-400">
                  Geen taken gepland.
                </div>
              )}

              {items.map((task) => (
                <TaskBlock key={task.id} task={task} />
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
