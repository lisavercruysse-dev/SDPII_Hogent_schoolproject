import { X } from "lucide-react";
import TaskForm from "./TaskForm";

export default function EditTimeBlockModal({
  isOpen,
  onClose,
  werknemers,
  task,
  onSaved,
  tasks
}) {
  if (!isOpen) return null;

  return (
    <div
      onClick={onClose}
      className="fixed inset-0 bg-black/30 backdrop-blur-sm flex items-center justify-center z-50 p-4"
    >
      <div
        onClick={(e) => e.stopPropagation()}
        className="bg-white rounded-xl shadow-xl w-full max-w-xl flex flex-col max-h-150"
      >
        <div className="border-b border-[#E5E5E5] p-6 flex flex-col gap-1 relative shrink-0">
          <button
            className="absolute top-4 right-4 text-gray-400 hover:text-gray-600 transition-colors"
            onClick={onClose}
          >
            <X className="w-5 h-5" />
          </button>
          <h2
            className="text-xl font-semibold text-gray-800"
            data-cy="taakWijzigenTitel"
          >
            {task.memberId ? "Taak Wijzigen" : "Taak Toewijzen"}
          </h2>
        </div>

        <div className="flex-1 overflow-y-auto px-6 py-4 flex flex-col gap-2">
          <p className="text-gray-400 mt-3 pl-8">
            De werknemer zal hiervan op de hoogte gebracht worden.
          </p>
          <TaskForm
            werknemers={werknemers}
            task={task}
            onClose={onClose}
            onSaved={onSaved}
            tasks={tasks}
          />
        </div>
      </div>
    </div>
  );
}
