import { X } from "lucide-react";
import { FaRegCalendar } from "react-icons/fa";
import { CiClock2 } from "react-icons/ci";
import useSWR from "swr";
import { getById } from "../../api";
import AsyncData from "../asyncData/AsyncData";
import { useForm } from "react-hook-form";

export default function TaskDetailsModal({ isOpen, onClose, task, type, onSubmit, mutate }) {
  const { register, handleSubmit, formState: { errors, isValid }, reset } = useForm({
    mode: 'onBlur',
  });

  const { data: taskDetails, isLoading, error } = useSWR(
    task?.id ? `taken/${task.id}/details` : null,
    getById
  );

  const validationRules = {
    tijdGespendeerd: {
      required: 'Geef het aantal gespendeerde minuten in',
      min: { value: 15, message: 'Gespendeerde tijd moet minstens 15 minuten zijn.' },
      max: { value: 480, message: 'Gespendeerde tijd kan niet langer dan 480 minuten zijn.' }
    }
  };

  const handleFormSubmit = async (values) => {
    let payload;

    if (type === "complete") {
      payload = {
        status: "afgewerkt",
        tijdGespendeerd: Number(values.tijdGespendeerd),
      };
    } else if (type === "cancel") {
      payload = {
        status: "gepland",
        tijdGespendeerd: 0,
      };
    } else {
      return;
    }

    await onSubmit(payload, {
      throwOnError: false,
      onSuccess: () => {
        if (mutate) {
          mutate((tasks) =>
            tasks.map((t) =>
              t.id === task.id ? { ...t, status: payload.status } : t
            ), false
          );
        }
        reset();
        onClose();
      },
    });
  };

  if (!isOpen || !task) return null;

  return (
    <div
      className="fixed inset-0 bg-black/30 backdrop-blur-sm flex items-center justify-center z-50 p-4"
      onClick={onClose}
    >
      <div
        className="bg-white rounded-xl shadow-xl w-full max-w-xl overflow-hidden flex flex-col"
        onClick={(e) => e.stopPropagation()}
      >
        <div className="border-b border-[#E5E5E5] p-6 flex flex-col gap-1 relative" data-cy="task_modal">
          <button
            className="absolute top-4 right-4 text-gray-400 hover:text-gray-600 transition-colors"
            onClick={onClose}
          >
            <X className="w-5 h-5" />
          </button>

          <h2 className="text-xl font-semibold text-gray-800" data-cy='task_modal_title'>
            {type === "complete" ? "Markeer taak als afgewerkt" : type === "cancel" ? "Markeer als onafgewerkt" : "Details taak"}
          </h2>
        </div>

        {type === "complete" ? (
          <div className="flex flex-col p-5 gap-2 items-start">
            <label className="text-sm text-gray-700">
              Tijd die nodig was om de taak af te werken
            </label>
            <form onSubmit={handleSubmit(handleFormSubmit)} className="w-full">
              <input
                {...register('tijdGespendeerd', validationRules.tijdGespendeerd)}
                data-cy='task_complete_input'
                type="number"
                placeholder="Aantal minuten"
                className="w-full bg-[#F3F3F5] rounded-md px-3 py-2 text-sm text-[#717182] focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
              {errors.tijdGespendeerd && <p className="text-red-500 text-sm mt-1">{errors.tijdGespendeerd.message}</p>}

              <div className="flex items-center justify-end gap-3 mt-4">
                <button
                  type="button"
                  onClick={onClose}
                  className="px-4 py-2 border border-gray-300 text-gray-700 rounded-md text-sm hover:bg-gray-50 transition-colors cursor-pointer"
                >
                  Annuleren
                </button>
                <button
                  type="submit"
                  className="cursor-pointer px-4 py-2 bg-[#4863d6] text-white rounded-md text-sm font-medium hover:bg-[#3c52b3] transition-colors"
                  data-cy="task_complete_bevestig_button"
                >
                  Bevestigen
                </button>
              </div>
            </form>
          </div>
        ) : type === "cancel" ? (
          <div className="flex flex-col p-5 gap-2 items-start">
            <p>
              Weet je zeker dat je de taak wil markeren als onafgewerkt?
              Al uw gespendeerde tijd zal gereset worden.
            </p>
            <div className="flex items-center justify-end gap-3 mt-4">
              <button
                className="px-4 py-2 border border-gray-300 text-gray-700 rounded-md text-sm hover:bg-gray-50 transition-colors cursor-pointer"
                onClick={onClose}
                data-cy='task_cancel_markeer_onafgewerkt'
              >
                Annuleren
              </button>
              <button
                className="cursor-pointer px-4 py-2 bg-[#4863d6] text-white rounded-md text-sm font-medium hover:bg-[#3c52b3] transition-colors"
                onClick={handleFormSubmit}
                data-cy='task_markeer_onafgewerkt'
              >
                Bevestigen
              </button>
            </div>
          </div>
        ) : (
          <AsyncData isLoading={isLoading} error={error}>
            <div className="flex gap-8">
              <div className="flex flex-col gap-5 p-6 w-94">
                <div>
                  <p>Omschrijving</p>
                  <p className="text-[#737373]" data-cy='taak_omschrijving'>{task.taakTemplate.omschrijving}</p>
                </div>

                <div>
                  <p>Specificaties</p>
                  <p className="text-[#737373] max-w-80" data-cy='taak_specificaties'>{task.specificaties}</p>
                </div>
              </div>

              <div className="flex flex-col gap-5 p-6">
                <div className="flex flex-col">
                  <div className="flex gap-3 items-center" data-cy='taak_startdatum'>
                    <FaRegCalendar />
                    {task.datum.split("T")[0]}
                  </div>

                  <div className="flex gap-3 text-[#737373] items-center" data-cy='taak_starttijd'>
                    <CiClock2 />
                    {task.datum.split("T")[1].slice(0, 5)}
                  </div>
                </div>

                <div>
                  <p>Type taak</p>
                  <p className="text-[#737373]" data-cy='taak_type'>{task.taakTemplate.type}</p>
                </div>

                <div>
                  <p>Afwerktijd</p>
                  <p className="text-[#737373]" data-cy='taak_duurtijd'>
                    {task.taakTemplate.duurTijd ? task.taakTemplate.duurTijd + " minuten" : "/"}
                  </p>
                </div>
              </div>
            </div>

            <div className="flex bg-[#F2F2F2] px-8 py-5 justify-between">
              <div>
                <p>Machine</p>
                <p className="text-[#737373]" data-cy='taak_machine'>{taskDetails?.machine?.machineName}</p>
              </div>

              <div>
                <p>Site</p>
                <p className="text-[#737373]" data-cy='taak_machine_sitelocatie'>{taskDetails?.machine?.siteName}</p>
              </div>

              <div>
                <p>Locatie In Site</p>
                <p className="text-[#737373]" data-cy='taak_machine_locatie'>{taskDetails?.machine?.locationOnSite}</p>
              </div>
            </div>
          </AsyncData>
        )}
      </div>
    </div>
  );
}