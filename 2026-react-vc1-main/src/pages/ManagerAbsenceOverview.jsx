import React, { useState } from "react";
import useSWR from "swr";
import { Search, Filter } from "lucide-react";
import { useAuth } from "../contexts/auth";
import * as absencesApi from "../api/absences";
import ManagerAbsenceRow from "../components/absences/ManagerAbsenceRow";
import ConfirmModal from "../components/absences/ConfirmModal";
import AsyncData from "../components/asyncData/AsyncData";

function formatDate(isoDate) {
  if (!isoDate) return "";
  return new Date(isoDate + "T00:00:00").toLocaleDateString("en-US", {
    month: "short",
    day: "numeric",
    year: "numeric",
  });
}

export default function ManagerAbsenceOverview() {
  const { user } = useAuth();

  const {
    data: rawAanvragen,
    isLoading,
    error,
    mutate,
  } = useSWR(
    user ? `afwezigheden-all-${user.id}` : null,
    absencesApi.getAllAbsences,
  );

  const aanvragen = (rawAanvragen ?? []).map((a) => ({
    ...a,
    startDate: formatDate(a.startDate),
    endDate: formatDate(a.endDate),
    days: `${a.days} ${a.days === 1 ? "dag" : "dagen"}`,
  }));

  const [searchTerm, setSearchTerm] = useState("");
  const [statusFilter, setStatusFilter] = useState("In behandeling");
  const [typeFilter, setTypeFilter] = useState("Alles");

  const [confirmModalOpen, setConfirmModalOpen] = useState(false);
  const [pendingAction, setPendingAction] = useState({
    id: null,
    status: null,
  });
  const [actionError, setActionError] = useState(null);

  const handleActionClick = (id, newStatus) => {
    setPendingAction({ id, status: newStatus });
    setConfirmModalOpen(true);
  };

  const executeAction = async () => {
    try {
      if (pendingAction.status === "Goedgekeurd") {
        await absencesApi.approveAbsence(pendingAction.id);
      } else {
        await absencesApi.rejectAbsence(pendingAction.id);
      }
      setActionError(null);
      mutate();
    } catch {
      setActionError("Er is een fout opgetreden. Probeer het opnieuw.");
    }
    setConfirmModalOpen(false);
    setPendingAction({ id: null, status: null });
  };

  const filteredAanvragen = aanvragen.filter((a) => {
    const matchesSearch = a.werknemer
      .toLowerCase()
      .includes(searchTerm.toLowerCase());
    const matchesStatus = statusFilter === "Alles" || a.status === statusFilter;
    const matchesType = typeFilter === "Alles" || a.type === typeFilter;
    return matchesSearch && matchesStatus && matchesType;
  });

  return (
    <div className="p-4 md:p-8 w-full max-w-6xl mx-auto font-sans">
      <div className="mb-6">
        <h1 className="text-xl md:text-2xl font-medium text-gray-800">
          Te keuren afwezigheden
        </h1>
        <p className="text-sm text-gray-500 mt-1">
          Beheer vakantie- en ziektemeldingen van uw team.
        </p>
      </div>

      <div className="flex flex-col lg:flex-row gap-4 mb-6">
        <div className="relative flex-1">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
          <input
            type="text"
            placeholder="Zoek op naam..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-[#4863d6] focus:border-transparent"
          />
        </div>

        <div className="flex items-center gap-3">
          <div className="flex items-center gap-2 text-gray-500 pl-1">
            <Filter className="w-4 h-4" />
            <span className="text-sm font-medium hidden sm:inline">
              Filters:
            </span>
          </div>

          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="w-full sm:w-48 px-3 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-[#4863d6] focus:border-transparent bg-white"
          >
            <option value="Alles">Alle statussen</option>
            <option value="In behandeling">In behandeling</option>
            <option value="Goedgekeurd">Goedgekeurd</option>
            <option value="Geweigerd">Geweigerd</option>
          </select>

          <select
            value={typeFilter}
            onChange={(e) => setTypeFilter(e.target.value)}
            className="w-full sm:w-48 px-3 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-[#4863d6] focus:border-transparent bg-white"
          >
            <option value="Alles">Alle types</option>
            <option value="Vakantie">Vakantie</option>
            <option value="Ziekte">Ziekte</option>
          </select>
        </div>
      </div>

      <div className="bg-white border border-gray-200 rounded-xl shadow-sm overflow-hidden mb-8">
        <div className="p-5 border-b border-gray-100 flex justify-between items-center">
          <h2 className="text-sm font-medium text-gray-800">
            Aanvragen ({filteredAanvragen.length})
          </h2>
        </div>
        <div className="p-5 flex flex-col gap-4">
          {actionError && (
            <div className="p-3 bg-red-50 border border-red-200 rounded-md text-sm text-red-700">
              {actionError}
            </div>
          )}
          <AsyncData loading={isLoading} error={error}>
            {filteredAanvragen.length === 0 ? (
              <p className="text-sm text-gray-500 text-center py-4">
                Er zijn geen aanvragen die voldoen aan uw criteria.
              </p>
            ) : (
              filteredAanvragen.map((item) => (
                <ManagerAbsenceRow
                  key={item.id}
                  item={item}
                  onAction={handleActionClick}
                />
              ))
            )}
          </AsyncData>
        </div>
      </div>

      <ConfirmModal
        isOpen={confirmModalOpen}
        onClose={() => setConfirmModalOpen(false)}
        onConfirm={executeAction}
        title={
          pendingAction.status === "Goedgekeurd"
            ? "Aanvraag goedkeuren"
            : "Aanvraag weigeren"
        }
        message={`Weet u zeker dat u deze aanvraag wilt ${pendingAction.status === "Goedgekeurd" ? "goedkeuren" : "weigeren"}?`}
        confirmText={
          pendingAction.status === "Goedgekeurd" ? "Goedkeuren" : "Weigeren"
        }
        cancelText="Annuleren"
        confirmColor={pendingAction.status === "Goedgekeurd" ? "blue" : "red"}
      />
    </div>
  );
}
