import React, { useState, useEffect } from "react";
import { X } from "lucide-react";

export default function AbsenceModal({
  isOpen,
  onClose,
  type,
  onSubmit,
  absences = [],
}) {
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [reason, setReason] = useState("");
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (isOpen) {
      setStartDate("");
      setEndDate("");
      setReason("");
      setErrors({});
    }
  }, [isOpen]);

  if (!isOpen) return null;

  const validate = () => {
    const newErrors = {};
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    const start = new Date(startDate);
    const end = new Date(endDate);
    start.setHours(0, 0, 0, 0);
    end.setHours(0, 0, 0, 0);

    if (!startDate) {
      newErrors.startDate = "Start datum is verplicht.";
    } else if (start < today) {
      newErrors.startDate = "Datum mag niet in het verleden liggen.";
    }

    if (!endDate) {
      newErrors.endDate = "Eind datum is verplicht.";
    } else if (end < today) {
      newErrors.endDate = "Datum mag niet in het verleden liggen.";
    } else if (startDate && end < start) {
      newErrors.endDate = "Eind datum mag niet voor start datum liggen.";
    }

    if (!reason.trim()) {
      newErrors.reason = "Reden is verplicht.";
    }

    if (startDate && endDate) {
      const hasOverlap = absences.some((a) => {
        if (a.status === "Geannuleerd" || a.status === "Geweigerd")
          return false;
        const aStart = new Date(a.startDate);
        const aEnd = new Date(a.endDate);
        aStart.setHours(0, 0, 0, 0);
        aEnd.setHours(0, 0, 0, 0);

        return start <= aEnd && end >= aStart;
      });

      if (hasOverlap) {
        newErrors.startDate =
          "Deze periode overlapt met een bestaande afwezigheid.";
      }
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (validate()) {
      onSubmit({ startDate, endDate, reason, type });
      onClose();
    }
  };

  const title = type === "Vakantie" ? "Vakantie aanvragen" : "Ziekte melden";
  const subtitle =
    type === "Vakantie"
      ? "Uw manager en verantwoordelijke worden op de hoogte gesteld."
      : "Meld u tijdig ziek zodat uw team op de hoogte is.";

  return (
    <div
      className="fixed inset-0 bg-black/30 backdrop-blur-sm flex items-center justify-center z-50 p-4"
      onClick={onClose}
    >
      <div
        className="bg-white rounded-xl shadow-xl w-full max-w-md overflow-hidden flex flex-col"
        onClick={(e) => e.stopPropagation()}
      >
        <div className="p-6 flex flex-col gap-1 relative">
          <button
            onClick={onClose}
            className="absolute top-4 right-4 text-gray-400 hover:text-gray-600 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
          <h2 className="text-xl font-semibold text-gray-800">{title}</h2>
          <p className="text-sm text-gray-500">{subtitle}</p>
        </div>

        <form onSubmit={handleSubmit} className="px-6 pb-6 flex flex-col gap-4">
          <div className="flex flex-col gap-1">
            <label className="text-sm text-gray-700">Start datum</label>
            <input
              type="date"
              value={startDate}
              onChange={(e) => setStartDate(e.target.value)}
              className={`w-full border rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent ${
                errors.startDate ? "border-red-500" : "border-gray-300"
              }`}
            />
            {errors.startDate && (
              <p className="text-xs text-red-500">{errors.startDate}</p>
            )}
          </div>

          <div className="flex flex-col gap-1">
            <label className="text-sm text-gray-700">Eind datum</label>
            <input
              type="date"
              value={endDate}
              onChange={(e) => setEndDate(e.target.value)}
              className={`w-full border rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent ${
                errors.endDate ? "border-red-500" : "border-gray-300"
              }`}
            />
            {errors.endDate && (
              <p className="text-xs text-red-500">{errors.endDate}</p>
            )}
          </div>

          <div className="flex flex-col gap-1">
            <label className="text-sm text-gray-700">Reden</label>
            <textarea
              rows="3"
              value={reason}
              onChange={(e) => setReason(e.target.value)}
              placeholder="Geef een korte reden op..."
              className={`w-full border rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none ${
                errors.reason ? "border-red-500" : "border-gray-300"
              }`}
            />
            {errors.reason && (
              <p className="text-xs text-red-500">{errors.reason}</p>
            )}
          </div>

          <div className="flex items-center justify-end gap-3 mt-4">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 border border-gray-300 text-gray-700 rounded-md text-sm hover:bg-gray-50 transition-colors"
            >
              Annuleren
            </button>
            <button
              type="submit"
              className="px-4 py-2 bg-[#4863d6] text-white rounded-md text-sm font-medium hover:bg-[#3c52b3] transition-colors"
            >
              Aanvragen
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
