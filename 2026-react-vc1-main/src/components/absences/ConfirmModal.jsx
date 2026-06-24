import React from "react";
import { X } from "lucide-react";

export default function ConfirmModal({
  isOpen,
  onClose,
  onConfirm,
  title,
  message,
  confirmText = "Ja, annuleren",
  cancelText = "Nee, behouden",
  confirmColor = "red",
}) {
  if (!isOpen) return null;

  const confirmColorClass =
    confirmColor === "blue"
      ? "bg-[#4863d6] hover:bg-[#3c52b3]"
      : "bg-[#b64949] hover:bg-[#9d3f3f]";

  return (
    <div
      className="fixed inset-0 bg-black/30 backdrop-blur-sm flex items-center justify-center z-50 p-4"
      onClick={onClose}
    >
      <div
        className="bg-white rounded-xl shadow-xl w-full max-w-sm overflow-hidden flex flex-col p-6 relative"
        onClick={(e) => e.stopPropagation()}
      >
        <button
          onClick={onClose}
          className="absolute top-4 right-4 text-gray-400 hover:text-gray-600 transition-colors"
        >
          <X className="w-5 h-5" />
        </button>

        <h2 className="text-xl font-semibold text-gray-800 mb-2">{title}</h2>
        <p className="text-sm text-gray-500 mb-6">{message}</p>

        <div className="flex items-center justify-end gap-3">
          <button
            onClick={onClose}
            className="px-4 py-2 border border-gray-300 text-gray-700 rounded-md text-sm hover:bg-gray-50 transition-colors"
          >
            {cancelText}
          </button>
          <button
            onClick={onConfirm}
            className={`px-4 py-2 text-white rounded-md text-sm font-medium transition-colors ${confirmColorClass}`}
          >
            {confirmText}
          </button>
        </div>
      </div>
    </div>
  );
}
