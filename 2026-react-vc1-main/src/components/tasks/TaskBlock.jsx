import { FiEdit2, FiTrash2 } from "react-icons/fi";
import { IoMdTime } from "react-icons/io";
import {
  TIMELINE_START,
  TIMELINE_END,
  TOTAL_HOURS,
  TASK_STATUS_COLORS,
  DEFAULT_TASK_COLOR,
} from "./TimelineConfig.js";
import { useAuth } from "../../contexts/auth";

function formatDuration(minutes) {
  const hours = Math.floor(minutes / 60);
  const mins = minutes % 60;
  if (hours === 0) return `${mins} min`;
  if (mins === 0) return `${hours} uur`;
  return `${hours}u${String(mins).padStart(2, "0")}`;
}

export function TaskBlock({ task, onEdit, onDelete }) {
  const { user } = useAuth();
  const normalizedRole = (user?.jobTitel ?? "").trim().toLowerCase();
  const isManagerOrVerantwoordelijke =
    normalizedRole === "verantwoordelijke" || normalizedRole === "manager";

  const start = new Date(task.datum);
  const durationHours = task.taakTemplate.duurTijd / 60;
  const startDecimal = start.getHours() + start.getMinutes() / 60;

  const clampedStart = Math.max(startDecimal, TIMELINE_START);
  const clampedEnd = Math.min(startDecimal + durationHours, TIMELINE_END);

  const left = ((clampedStart - TIMELINE_START) / TOTAL_HOURS) * 100;
  const width = ((clampedEnd - clampedStart) / TOTAL_HOURS) * 100;

  const endDate = new Date(
    start.getTime() + task.taakTemplate.duurTijd * 60000,
  );
  const timeLabel = `${start.getHours()}:${String(start.getMinutes()).padStart(2, "0")} - ${endDate.getHours()}:${String(endDate.getMinutes()).padStart(2, "0")}`;

  const colorClass = TASK_STATUS_COLORS[task.status] || DEFAULT_TASK_COLOR;

  return (
    <div
      key={task.id}
      title={`${task.taakTemplate.type}: ${task.taakTemplate.omschrijving}
      Tijd: ${timeLabel}
      Duur: ${formatDuration(task.taakTemplate.duurTijd)}
      Status: ${task.status}
      `}
      className={`absolute top-0.5 bottom-0.5 rounded border px-2 py-1 overflow-hidden ${colorClass}`}
      style={{ left: `${left}%`, width: `${width}%` }}
    >
      <p className="text-xs font-semibold truncate">
        {task.taakTemplate.omschrijving}
      </p>
      <p className="text-[11px] opacity-75 mt-0.5">{timeLabel}</p>
      <div className="flex items-center">
        <IoMdTime className="mr-1" />
        <p className="text-[11px] opacity-75 mt-0.5">
          {formatDuration(task.taakTemplate.duurTijd)} - {task.status}
        </p>
      </div>

      {isManagerOrVerantwoordelijke && (
        <div className="absolute bottom-1 right-1 gap-1">
          <button
            onClick={(e) => {
              e.stopPropagation();
              onEdit(task);
            }}
            className={`rounded p-0.5 ${colorClass}`}
            title="Bewerken"
            data-cy="editTaskButton"
          >
            <FiEdit2 size={11} />
          </button>

          <button
            onClick={(e) => {
              e.stopPropagation();
              onDelete(task);
            }}
            className={`rounded p-0.5 ${colorClass}`}
            title="Verwijderen"
          >
            <FiTrash2 size={11} />
          </button>
        </div>
      )}
    </div>
  );
}
