import { memo } from "react";
import { IoMdTime } from "react-icons/io";

function getColorClass(letter) {
  const colors = [
    "bg-red-100 text-red-800",
    "bg-yellow-100 text-yellow-800",
    "bg-green-100 text-green-800",
    "bg-blue-100 text-blue-800",
    "bg-orange-100 text-orange-800",
  ];

  const code = letter.toUpperCase().charCodeAt(0);
  const index = code % colors.length;

  return colors[index];
}

const timeFormat = new Intl.DateTimeFormat("nl-BE", {
  hour: "2-digit",
  minute: "2-digit",
});

function formatIsoTime(isoString) {
  if (!isoString) return "";
  return isoString.split("T")[1]?.slice(0, 5) ?? "";
}

function addMinutesToIsoTime(isoString, minutes) {
  const timePart = isoString?.split("T")[1]?.slice(0, 5);
  if (!timePart) return "";

  const [hours, mins] = timePart.split(":").map(Number);
  const totalMinutes = hours * 60 + mins + minutes;
  const normalizedMinutes = ((totalMinutes % 1440) + 1440) % 1440;

  return `${String(Math.floor(normalizedMinutes / 60)).padStart(2, "0")}:${String(normalizedMinutes % 60).padStart(2, "0")}`;
}

const TaskCardMemoized = memo(function TaskCard({
  id,
  type,
  omschrijving,
  duurtijd,
  datum,
  status,
  task,
  onDetailsClick,
  onCompleted,
  onCancel,
}) {
  if (!datum) return null;
  const normalizedStatus = (status ?? "").toLowerCase();
  const startTime = formatIsoTime(datum);
  const endTime = addMinutesToIsoTime(datum, duurtijd);

  return (
    <div className="p-4 h-29.25 flex " data-cy="task">
      {/* markeer knop */}
      <div className="min-w-7.5">
        <button
          className="w-4 h-4 bg-[#F3F3F5] rounded-sm border border-black/10 cursor-pointer flex items-center justify-center text-xs"
          onClick={normalizedStatus === "afgewerkt" ? onCancel : onCompleted}
          data-cy="complete_button"
        >
          {normalizedStatus === "afgewerkt" ? "x" : ""}
        </button>
      </div>

      {/* taak inhoud */}
      <div className="flex w-full justify-between">
        {/* details van taak */}
        <div>
          <p className="card-title mb-2" data-cy="task_description">
            {omschrijving}
          </p>
          <div className="flex card-text items-center">
            <IoMdTime className="mr-1" />
            <p className="" data-cy="task_time">
              {`${startTime} - ${endTime}`}
            </p>
          </div>
          <button
            className="bg-[#90A1B9] rounded-lg px-1.75 py-0.5 mt-2 text-white hover:cursor-pointer hover:bg-[#B7C2D2]"
            onClick={onDetailsClick}
            data-cy="detail_button"
          >
            Details
          </button>
        </div>

        {/* type van taak */}
        <div className="flex items-center">
          <p
            data-cy="task_type"
            className={`h-fit px-2 py-1 rounded-sm ${getColorClass(type[0])}`}
          >
            {type}
          </p>
        </div>
      </div>
    </div>
  );
});

export default TaskCardMemoized;