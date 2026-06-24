import { TaskBlock } from "./TaskBlock";
import { TOTAL_HOURS } from "./TimelineConfig.js";

export function MemberRow({ member, tasks, onEdit, onDelete }) {
  const doneTasks = tasks.filter(
    (t) => t.status?.toLowerCase() === "afgewerkt",
  ).length;
  const pendingTasks = tasks.filter(
    (t) => t.status?.toLowerCase() === "gepland",
  ).length;

  return (
    <div className="flex border-b border-gray-100 hover:bg-gray-50 transition-colors group">
      {/* Member info */}
      <div className="w-40 shrink-0 flex items-center gap-3 px-3 py-3 border-r border-gray-200">
        <div className="min-w-0">
          <p className="text-sm font-medium text-gray-800 truncate">
            {member.firstName}
          </p>
          <p className="text-sm font-medium text-gray-800 truncate">
            {member.lastName}
          </p>
          <p className="text-[11px] text-gray-400 mt-0.5">
            {tasks.length} {tasks.length !== 1 ? "taken" : "taak"} · {doneTasks}{" "}
            klaar
          </p>
        </div>
      </div>

      {/* Timeline */}
      <div className="flex-1 relative min-h-16">
        {/* Grid lines */}
        {Array.from({ length: TOTAL_HOURS + 1 }, (_, i) => (
          <div
            key={i}
            className="absolute top-0 bottom-0 border-l border-gray-100"
            style={{ left: `${(i / TOTAL_HOURS) * 100}%` }}
          />
        ))}

        {/* Empty state */}
        {tasks.length === 0 && (
          <div className="absolute inset-0 flex items-center pl-4 text-xs text-gray-300 italic">
            Geen taken gepland
          </div>
        )}

        {/* Task blocks */}
        {tasks.map((task) => (
          <TaskBlock
            key={task.id}
            task={task}
            onEdit={onEdit}
            onDelete={onDelete}
          />
        ))}
      </div>
    </div>
  );
}
