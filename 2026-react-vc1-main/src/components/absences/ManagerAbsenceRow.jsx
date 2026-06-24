import React from "react";
import { Calendar, User, AlignLeft, Check, X } from "lucide-react";

export default function ManagerAbsenceRow({ item, onAction }) {
  return (
    <div className="flex flex-col md:flex-row items-start md:items-center justify-between p-5 bg-white rounded-lg border border-gray-200 shadow-sm gap-4">
      <div className="flex flex-col gap-3 w-full md:w-2/3">
        <div className="flex items-center gap-2">
          <User className="w-4 h-4 text-gray-400" />
          <span className="text-sm font-medium text-gray-800">
            {item.werknemer}
          </span>
          <span
            className={`px-2 py-0.5 rounded text-xs ml-2 ${
              item.type === "Vakantie"
                ? "bg-[#f3e8ff] text-[#7e22ce]"
                : "bg-[#fee2e2] text-[#b91c1c]"
            }`}
          >
            {item.type}
          </span>
          {item.status !== "In behandeling" && (
            <span
              className={`px-2 py-0.5 rounded text-xs ml-2 ${
                item.status === "Goedgekeurd"
                  ? "bg-green-100 text-green-700"
                  : "bg-red-100 text-red-700"
              }`}
            >
              {item.status}
            </span>
          )}
        </div>

        <div className="flex items-center gap-2">
          <Calendar className="w-4 h-4 text-gray-400" />
          <span className="text-sm text-gray-600">
            {item.startDate} - {item.endDate} ({item.days})
          </span>
        </div>

        <div className="flex items-start gap-2">
          <AlignLeft className="w-4 h-4 text-gray-400 mt-0.5" />
          <span className="text-sm text-gray-500 italic">"{item.reason}"</span>
        </div>
      </div>

      {item.status === "In behandeling" && (
        <div className="flex items-center gap-3 w-full md:w-auto mt-2 md:mt-0">
          <button
            onClick={() => onAction(item.id, "Geweigerd")}
            className="flex-1 md:flex-none flex items-center justify-center gap-2 px-4 py-2 border border-[#b64949] text-[#b64949] rounded-md text-sm font-medium hover:bg-[#fee2e2] transition-colors"
          >
            <X className="w-4 h-4" />
            Weigeren
          </button>
          <button
            onClick={() => onAction(item.id, "Goedgekeurd")}
            className="flex-1 md:flex-none flex items-center justify-center gap-2 px-4 py-2 bg-[#4863d6] text-white rounded-md text-sm font-medium hover:bg-[#3c52b3] transition-colors"
          >
            <Check className="w-4 h-4" />
            Goedkeuren
          </button>
        </div>
      )}
    </div>
  );
}
