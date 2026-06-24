import React from "react";
import { Calendar } from "lucide-react";

export default function AbsenceRow({ item, onCancel }) {
  return (
    <div
      data-cy="absence-row"
      className="flex flex-col sm:flex-row items-start sm:items-center justify-between p-4 bg-[#f8f9fa] rounded-lg border border-gray-100 gap-4"
    >
      <div className="flex items-start gap-3">
        <Calendar className="w-5 h-5 text-gray-400 mt-0.5" strokeWidth={1.5} />
        <div>
          <p className="text-sm text-gray-800">
            {item.startDate} - {item.endDate}
          </p>
          <p className="text-xs text-gray-500 mt-1">{item.days}</p>
        </div>
      </div>
      <div className="flex flex-wrap items-center gap-2 w-full sm:w-auto mt-2 sm:mt-0">
        {item.type === "Vakantie" && (
          <span className="px-2 py-1 bg-[#f3e8ff] text-[#7e22ce] rounded text-xs">
            Vakantie
          </span>
        )}
        {item.type === "Ziekte" && (
          <span className="px-2 py-1 bg-[#fee2e2] text-[#b91c1c] rounded text-xs">
            Ziekte
          </span>
        )}
        {item.status === "In behandeling" && (
          <span className="px-2 py-1 bg-[#fef3c7] text-[#b45309] rounded text-xs">
            In behandeling
          </span>
        )}
        {item.status === "Goedgekeurd" && (
          <span className="px-2 py-1 bg-[#dcfce7] text-[#166534] rounded text-xs">
            Goedgekeurd
          </span>
        )}
        {item.status === "Geweigerd" && (
          <span className="px-2 py-1 bg-[#fee2e2] text-[#991b1b] rounded text-xs">
            Geweigerd
          </span>
        )}
        {item.status === "Geannuleerd" && (
          <span className="px-2 py-1 bg-[#f3f4f6] text-[#374151] rounded text-xs">
            Geannuleerd
          </span>
        )}
        {item.canCancel && (
          <button
            onClick={() => onCancel(item.id)}
            className="px-3 py-1 bg-white border border-gray-300 text-gray-700 rounded text-xs hover:bg-gray-50 transition-colors ml-auto sm:ml-2"
          >
            Annuleren
          </button>
        )}
      </div>
    </div>
  );
}
