import React from "react";
import { X } from "lucide-react";

export default function ErrorModal({ isOpen, onClose, title, message }) {
  if (!isOpen) return null;

  return (
    <div
      className="fixed inset-0 bg-black/30 backdrop-blur-sm flex items-center justify-center z-50 p-4"
      onClick={onClose}
    >
      <div
        className="bg-white rounded-xl shadow-xl w-full max-w-sm overflow-hidden flex flex-col items-center p-6 relative"
        onClick={(e) => e.stopPropagation()}
      >
        <button
          onClick={onClose}
          className="absolute top-4 right-4 text-gray-400 hover:text-gray-600 transition-colors"
        >
          <X className="w-5 h-5" />
        </button>

        <div className="w-16 h-16 bg-[#b64949] rounded-full flex items-center justify-center mb-4 mt-2">
          <X className="w-8 h-8 text-white" strokeWidth={2.5} />
        </div>

        <h2 className="text-xl font-semibold text-gray-800 text-center mb-2">
          {title}
        </h2>
        <p className="text-sm text-gray-500 text-center mb-6 px-4">{message}</p>

        <button
          onClick={onClose}
          className="px-8 py-2 bg-black text-white rounded-md text-sm font-medium hover:bg-gray-800 transition-colors"
        >
          OK
        </button>
      </div>
    </div>
  );
}
