import { useForm } from "react-hook-form";
import { MACHINE_DATA } from "../../api/mock_data";
import { post, updateById } from "../../api";
import { useState } from "react";
import ErrorModal from "../absences/ErrorModal";

function getIsoTime(isoDateTime) {
  if (!isoDateTime) return "";
  return isoDateTime.split("T")[1]?.slice(0, 5) ?? "";
}

function addMinutesToIsoTime(isoDateTime, minutes) {
  if (!isoDateTime) return "";

  const timePart = isoDateTime.split("T")[1]?.slice(0, 5);
  if (!timePart) return "";

  const [hours, mins] = timePart.split(":").map(Number);
  const totalMinutes = hours * 60 + mins + minutes;
  const normalizedMinutes = ((totalMinutes % 1440) + 1440) % 1440;

  return `${String(Math.floor(normalizedMinutes / 60)).padStart(2, "0")}:${String(normalizedMinutes % 60).padStart(2, "0")}`;
}

const validationRules = {
  werknemer: {
    required: "Een werknemer moet aangeduid worden.",
  },
  machine: {
    required: "Een machine moet aangeduid worden.",
  },
  datum: {
    required: "Er moet een datum gekozen worden.",
    validate: (value) => {
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      const selectedDate = new Date(value);
      return selectedDate >= today || "Datum mag niet in het verleden liggen";
    },
  },
  starttijd: {
    required: "Er moet een starttijd gekozen worden.",
    validate: (value) => {
      if (!value) return true;
      return (
        (value >= "08:00" && value <= "17:00") ||
        "Starttijd moet tussen 08:00 en 17:00 liggen"
      );
    },
  },
  eindtijd: {
    required: "Er moet een eindtijd gekozen worden.",
    validate: (value, allValues) => {
      if (!value) return true;
      const starttijd = allValues.starttijd;
      if (value > "17:00") return "Eindtijd mag niet later dan 17:00 zijn";
      if (starttijd && value < starttijd)
        return "Eindtijd mag niet voor starttijd liggen";
      return true;
    },
  },
  specificaties: {
    required: "Er moeten meer specificaties gegeven worden.",
  },
  omschrijving: {
    required: "Er moet een omschrijving opgegeven worden.",
  },
};

export default function TaskForm({ werknemers, task, onClose, onSaved, tasks }) {
  const machines = MACHINE_DATA;
  const [errorIsOpen, setErrorIsOpen] = useState(false);
  const [submitError, setSubmitError] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const onErrorClose = () => {
    setErrorIsOpen(false)
  }

  const {
    register,
    handleSubmit,
    formState: { errors, isValid },
    reset,
  } = useForm({
    mode: "onBlur",
    defaultValues: {
      werknemer: task?.memberId || "",
      machine: task?.machine,
      datum: task?.startdatum ? task.startdatum.split("T")[0] : "",
      starttijd: task?.startdatum ? getIsoTime(task.startdatum) : "",
      eindtijd:
        task?.startdatum && task?.duurtijd
          ? addMinutesToIsoTime(task.startdatum, task.duurtijd)
          : "",
      specificaties: task?.specificaties,
      omschrijving: task?.omschrijving,
    },
  });

  const onSubmit = async (values) => {
    if (!isValid) return;
    console.log("onsubmit ran")

    const werknemerId = Number(values.werknemer);
    const newStart = new Date(`${values.datum}T${values.starttijd}:00`);
    const newEnd = new Date(`${values.datum}T${values.eindtijd}:00`);

    const hasOverlap = tasks.some((t) => {
        if (task?.memberId && t.id === task.id) return false;
        if (t.memberId !== werknemerId) return false;

        const tStart = new Date(t.startdatum);
        const tDateLocal = `${tStart.getFullYear()}-${String(tStart.getMonth() + 1).padStart(2, "0")}-${String(tStart.getDate()).padStart(2, "0")}`;
        if (tDateLocal !== values.datum) return false;

        const tEnd = new Date(tStart.getTime() + t.duurtijd * 60000);
        return newStart < tEnd && newEnd > tStart;
    })

    if (hasOverlap) {
      setErrorIsOpen(true)
      console.log("hasoverlap ran")
      return;
    }

    setSubmitError(null);
    setIsSubmitting(true);

    try {
      const selectedDateTime = new Date(
        `${values.datum}T${values.starttijd}:00`,
      );
      const fallbackTemplateId = task?.memberId
        ? task?.taakTemplate?.id
        : task?.id;
      const taakTemplateId = Number(
        task?.taakTemplate?.id ?? task?.taakTemplateId ?? fallbackTemplateId,
      );

      if (!taakTemplateId) {
        throw new Error("Geen geldig taaktemplate gevonden.");
      }

      const payload = {
        taakTemplateId,
        werknemerId: Number(values.werknemer),
        machineId: Number(values.machine),
        datum: selectedDateTime.toISOString(),
        specificaties: values.specificaties,
      };

      if (task?.memberId) {
        await updateById(`taken/${task.id}`, { arg: payload });
      } else {
        await post("taken", { arg: payload });
      }

      if (onSaved) {
        await onSaved();
      }

      reset();
      onClose();
    } catch (error) {
      setSubmitError(
        error?.response?.data?.message ||
          error.message ||
          "Kon taak niet opslaan.",
      );
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div>
        <form onSubmit={handleSubmit(onSubmit)}>
        <div className="flex flex-col gap-5  mx-8">
          <div className="flex flex-col gap-1">
            <label htmlFor="omschrijving">Omschrijving</label>
            <input
              className="p-2 border border-[#e4e4e4] rounded-lg text-sm focus:outline-none"
              type="text"
              data-cy="taakBewerkenOmschrijving"
              {...register("omschrijving", validationRules.omschrijving)}
            />
            {errors.omschrijving && (
              <p className="text-red-500 text-sm mt-1">
                {errors.omschrijving.message}
              </p>
            )}
          </div>
          <div className="flex flex-col gap-1">
            <label htmlFor="werknemer">Werknemer</label>
            <select
              className="bg-[#F3F3F5] p-2 rounded-lg"
              data-cy="taakBewerkenWerknemer"
              {...register("werknemer", validationRules.werknemer)}
            >
              <option value="">Kies een werknemer</option>
              {werknemers.map((w) => (
                <option key={w.id} value={w.id}>
                  {w.firstName + " " + w.lastName}
                </option>
              ))}
            </select>
            {errors.werknemer && (
              <p className="text-red-500 text-sm mt-1">
                {errors.werknemer.message}
              </p>
            )}
          </div>
          <div className="flex flex-col gap-1">
            <label htmlFor="machine">Machine</label>
            <select
              className="bg-[#F3F3F5] p-2 rounded-lg"
              {...register("machine", validationRules.machine)}
            >
              {machines.map((m) => (
                <option key={m.id} value={m.id}>
                  {m.name}
                </option>
              ))}
            </select>
            {errors.machine && (
              <p className="text-red-500 text-sm mt-1">
                {errors.machine.message}
              </p>
            )}
          </div>
          <div className="flex flex-col gap-1">
            <label htmlFor="datum">Datum</label>
            <div className="flex items-center gap-1.5 border border-gray-200 rounded-lg px-3 py-1.5 bg-white min-w-42.5">
              <input
                type="date"
                className="bg-transparent text-sm
              outline-none cursor-pointer min-w-27.5"
                data-cy="taakBewerkenDatum"
                {...register("datum", validationRules.datum)}
              />
              {errors.datum && (
                <p className="text-red-500 text-sm mt-1">
                  {errors.datum.message}
                </p>
              )}
            </div>
          </div>
          <div className="flex flex-col gap-1">
            <label htmlFor="starttijd">Starttijd</label>
            <input
              {...register("starttijd", validationRules.starttijd)}
              type="time"
              className="bg-[#F3F3F5] rounded-lg p-2"
              data-cy="taakBewerkenStarttijd"
            />
            {errors.starttijd && (
              <p className="text-red-500 text-sm mt-1">
                {errors.starttijd.message}
              </p>
            )}
          </div>
          <div className="flex flex-col gap-1">
            <label htmlFor="eindtijd">Eindtijd</label>
            <input
              {...register("eindtijd", validationRules.eindtijd)}
              type="time"
              className="bg-[#F3F3F5] rounded-lg p-2"
              data-cy="taakBewerkenEindtijd"
            />
            {errors.eindtijd && (
              <p className="text-red-500 text-sm mt-1">
                {errors.eindtijd.message}
              </p>
            )}
          </div>
          <div className="flex flex-col gap-1">
            <label htmlFor="specificaties">Specificaties</label>
            <textarea
              {...register("specificaties", validationRules.specificaties)}
              data-cy="taakBewerkenSpecificaties"
              className="min-h-25 p-3 border border-[#e4e4e4] rounded-lg text-sm resize-y focus:outline-none"
            />
            {errors.specificaties && (
              <p className="text-red-500 text-sm mt-1">
                {errors.specificaties.message}
              </p>
            )}
          </div>
        </div>
        {submitError && (
          <p className="mx-8 mt-2 text-red-600 text-sm">{submitError}</p>
        )}
        <div className="flex justify-end m-4 mx-8">
          <button
            data-cy="taakBewerkenSubmit"
            type="submit"
            disabled={isSubmitting}
            className="cursor-pointer px-4 py-2 bg-[#4863d6] text-white rounded-md text-sm font-medium hover:bg-[#3c52b3] transition-colors"
          >
            {isSubmitting
              ? "Opslaan..."
              : task?.memberId
                ? "Bewerken"
                : "Toewijzen"}
          </button>
        </div>
      </form>
      <ErrorModal 
        isOpen={errorIsOpen}
        onClose={onErrorClose}
        title="Toewijzen gefaald"
        message="Deze werknemer heeft al een taak op dat moment"
      />
    </div>
    
  );
}