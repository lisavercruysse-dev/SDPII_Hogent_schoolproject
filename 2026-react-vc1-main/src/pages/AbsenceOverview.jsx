import React, { useState } from "react";
import useSWR from "swr";
import StatCard from "../components/absences/StatCard";
import AbsenceRow from "../components/absences/AbsenceRow";
import AbsenceModal from "../components/absences/AbsenceModal";
import ErrorModal from "../components/absences/ErrorModal";
import ConfirmModal from "../components/absences/ConfirmModal";
import AsyncData from "../components/asyncData/AsyncData";
import { useAuth } from "../contexts/auth";
import * as absencesApi from "../api/absences";
import { Plane, PlusSquare } from "lucide-react";

function formatDate(isoDate) {
  if (!isoDate) return "";
  return new Date(isoDate + "T00:00:00").toLocaleDateString("en-US", {
    month: "short",
    day: "numeric",
    year: "numeric",
  });
}

export default function AbsenceOverview() {
  const { user } = useAuth();

  const { data, isLoading, error, mutate } = useSWR(
    user ? `afwezigheden-me-${user.id}` : null,
    absencesApi.getMyAbsences,
  );

  const rawAbsences = data?.absences ?? [];
  const stats = data?.stats ?? {
    totaleZiektedagen: 0,
    totaleVakantiedagen: 0,
  };

  const absences = rawAbsences.map((a) => ({
    ...a,
    startDate: formatDate(a.startDate),
    endDate: formatDate(a.endDate),
    days: `${a.days} ${a.days === 1 ? "dag" : "dagen"}`,
  }));

  const [submitError, setSubmitError] = useState(null);

  const [modalOpen, setModalOpen] = useState(false);
  const [modalType, setModalType] = useState("");

  const [errorModalOpen, setErrorModalOpen] = useState(false);

  const [cancelModalOpen, setCancelModalOpen] = useState(false);
  const [absenceIdToCancel, setAbsenceIdToCancel] = useState(null);

  const handleOpenModal = (type) => {
    if (type === "Ziekte") {
      const today = new Date();
      today.setHours(0, 0, 0, 0);

      const isAlreadySick = absences.some((a) => {
        if (a.type !== "Ziekte") return false;
        if (a.status === "Geannuleerd" || a.status === "Geweigerd")
          return false;

        const endDate = new Date(a.endDate);
        endDate.setHours(0, 0, 0, 0);

        return endDate >= today;
      });

      if (isAlreadySick) {
        setErrorModalOpen(true);
        return;
      }
    }

    setModalType(type);
    setModalOpen(true);
  };

  const handleCloseModal = () => {
    setModalOpen(false);
    setModalType("");
  };

  const handleSubmitAbsence = async (formData) => {
    const startDate = formData.startDate;
    const endDate = formData.endDate;
    const days =
      Math.ceil(
        (new Date(endDate) - new Date(startDate)) / (1000 * 60 * 60 * 24),
      ) + 1;

    try {
      await absencesApi.createAbsence({
        startDate,
        endDate,
        days,
        type: formData.type,
        reason: formData.reason,
      });
      setSubmitError(null);
      mutate();
    } catch {
      setSubmitError(
        "Er is een fout opgetreden bij het indienen van uw aanvraag. Probeer het opnieuw.",
      );
    }
  };

  const handleCancelClick = (id) => {
    setAbsenceIdToCancel(id);
    setCancelModalOpen(true);
  };

  const handleConfirmCancel = async () => {
    try {
      await absencesApi.cancelAbsence(absenceIdToCancel);
      mutate();
    } catch {
      setSubmitError(
        "Er is een fout opgetreden bij het annuleren. Probeer het opnieuw.",
      );
    }
    setCancelModalOpen(false);
    setAbsenceIdToCancel(null);
  };

  return (
    <div className="p-4 md:p-8 w-full max-w-6xl mx-auto font-sans">
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-6 gap-4">
        <h1 className="text-xl md:text-2xl font-medium text-gray-800">
          Beheer afwezigheden
        </h1>
        <div className="flex flex-col sm:flex-row gap-3 w-full md:w-auto">
          <button
            onClick={() => handleOpenModal("Ziekte")}
            className="flex items-center justify-center gap-2 bg-[#b64949] text-white px-4 py-2 rounded-md hover:bg-[#9d3f3f] transition-colors text-sm font-medium"
          >
            <PlusSquare className="w-4 h-4" />
            Ziekte melden
          </button>
          <button
            onClick={() => handleOpenModal("Vakantie")}
            className="flex items-center justify-center gap-2 bg-[#4863d6] text-white px-4 py-2 rounded-md hover:bg-[#3c52b3] transition-colors text-sm font-medium"
          >
            <Plane className="w-4 h-4" />
            Vakantie aanvragen
          </button>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-8">
        <StatCard
          label="Totale ziektedagen dit jaar"
          value={stats.totaleZiektedagen}
        />
        <StatCard
          label="Totale vakantiedagen dit jaar"
          value={stats.totaleVakantiedagen}
        />
      </div>

      {submitError && (
        <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-md text-sm text-red-700">
          {submitError}
        </div>
      )}

      <div className="bg-white border border-gray-200 rounded-xl shadow-sm overflow-hidden">
        <div className="p-5 border-b border-gray-100">
          <h2 className="text-sm font-medium text-gray-800">
            Afwezigheden overzicht
          </h2>
        </div>
        <div className="p-5 flex flex-col gap-4">
          <AsyncData loading={isLoading} error={error}>
            {absences.length === 0 ? (
              <p className="text-sm text-gray-500 text-center py-4">
                U heeft nog geen afwezigheden geregistreerd.
              </p>
            ) : (
              absences.map((item) => (
                <AbsenceRow
                  key={item.id}
                  item={item}
                  onCancel={handleCancelClick}
                />
              ))
            )}
          </AsyncData>
        </div>
      </div>

      <AbsenceModal
        isOpen={modalOpen}
        onClose={handleCloseModal}
        type={modalType}
        onSubmit={handleSubmitAbsence}
        absences={absences}
      />

      <ErrorModal
        isOpen={errorModalOpen}
        onClose={() => setErrorModalOpen(false)}
        title="Ziekte melden gefaald"
        message="U bent al ziek gemeld. Het is daarom niet mogelijk om opnieuw ziek te melden."
      />

      <ConfirmModal
        isOpen={cancelModalOpen}
        onClose={() => setCancelModalOpen(false)}
        onConfirm={handleConfirmCancel}
        title="Afwezigheid annuleren"
        message="Weet u zeker dat u deze afwezigheidsaanvraag wilt annuleren? Deze actie kan niet ongedaan worden gemaakt."
      />
    </div>
  );
}
