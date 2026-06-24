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

export default function UncompletedTask({ task, onAssign }) {
  const omschrijving =
    task.omschrijving || task.taakTemplate?.omschrijving || "Onbekend";
  const duurtijd = task.duurtijd ?? task.taakTemplate?.duurTijd ?? 0;
  const startdatum = task.startdatum || task.datum || "";
  const type = task.type || task.taakTemplate?.type || "Onbekend";

  return (
    <div className="grid grid-cols-[2fr_1fr_1fr_1fr_200px] items-center py-3 border-b border-[#F5F5F5] text-sm">
      <p data-cy="uncompletedTaskOmschrijving" className="truncate min-w-50">
        {omschrijving}
      </p>

      <p data-cy="uncompletedTaskMinuten" className="min-w-50">
        {duurtijd} min
      </p>

      <p data-cy="uncompletedTaskMinuten" className="min-w-50">
        {startdatum ? startdatum.split("T")[0] : "/"}
      </p>

      <div className="min-w-50">
        <span
          data-cy="unCompletedTaskType"
          className={`px-2 py-1 rounded-sm text-xs font-medium ${getColorClass(
            type[0],
          )}`}
        >
          {type}
        </span>
      </div>

      <div className="flex justify-end min-w-50">
        <button
          data-cy="unCompletedTaskToewijzen"
          className="px-3 py-1 border border-[#E5E5E5] rounded-lg hover:bg-gray-50 cursor-pointer"
          onClick={() => onAssign(task)}
        >
          Opnieuw inplannen
        </button>
      </div>
    </div>
  );
}
