import { FiFilter } from "react-icons/fi";
import { DatePicker } from "./DatePicker";
import { useAuth } from "../contexts/auth";

export function FilterBar({
  plants,
  plant,
  onPlant,
  teams,
  team,
  onTeam,
  selectedDate,
  setSelectedDate,
}) {
  const { user } = useAuth();
  const normalizedRole = (user?.jobTitel ?? "").trim().toLowerCase();
  const isManager = normalizedRole === "manager";
  const isVerantwoordelijke = normalizedRole === "verantwoordelijke";
  const isManagerOrVerantwoordelijke = isManager || isVerantwoordelijke;

  return (
    <div className="flex flex-wrap items-center gap-3 mb-5">
      {/* Plant filter */}
      {isManager && (
        <div className="flex items-center gap-1.5 border border-gray-200 rounded-lg px-3 py-1.5 bg-white shadow-sm">
          <FiFilter className="text-gray-400 text-xs" />
          <select
            value={plant}
            onChange={(e) => onPlant(e.target.value)}
            className="text-sm text-gray-700 bg-transparent outline-none cursor-pointer"
          >
            {plants.map((p) => (
              <option key={p.id} value={p.id}>
                {p.name}
              </option>
            ))}
          </select>
        </div>
      )}

      {/* Team filter */}
      {isManagerOrVerantwoordelijke && (
        <div className="flex items-center gap-1.5 border border-gray-200 rounded-lg px-3 py-1.5 bg-white shadow-sm">
          <FiFilter className="text-gray-400 text-xs" />
          <select
            data-cy="teamSelector"
            value={team}
            onChange={(e) => onTeam(e.target.value)}
            className="text-sm text-gray-700 bg-transparent outline-none cursor-pointer"
          >
            {teams.map((t) => (
              <option key={t.id} value={t.id}>
                {t.name}
              </option>
            ))}
          </select>
        </div>
      )}

      {/* Date selector */}
      <DatePicker
        selectedDate={selectedDate}
        setSelectedDate={setSelectedDate}
      />
    </div>
  );
}
