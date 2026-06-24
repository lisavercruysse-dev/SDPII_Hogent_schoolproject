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

export default function TaskTemplate({ taskTemplate, onAssign }) {
  return (
    <div className="grid grid-cols-[2fr_1fr_1fr_200px] items-center py-3 border-b border-[#F5F5F5] text-sm">

      <p data-cy="taskTemplateOmschrijving" className="truncate min-w-50">
        {taskTemplate.omschrijving}
      </p>

      <p data-cy="taskTemplateMinuten" className="min-w-50">
        {taskTemplate.duurTijd} min
      </p>

      <div className="min-w-50">
        <span data-cy="taskTemplateType"
          className={`px-2 py-1 rounded-sm text-xs font-medium ${getColorClass(
            taskTemplate.type[0]
          )}`}
        >
          {taskTemplate.type}
        </span>
      </div>

      <div className="flex justify-end min-w-50">
        <button data-cy="taskTemplateToewijzen" className="px-3 py-1 border border-[#E5E5E5] rounded-lg hover:bg-gray-50 cursor-pointer"
          onClick={() => onAssign(taskTemplate)}
        >
          Toewijzen
        </button>
      </div>

    </div>
  );
}