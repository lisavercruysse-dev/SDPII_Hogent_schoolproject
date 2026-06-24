// DashManagerDetail.jsx
import { useState } from "react";
import { ArrowLeft, MapPin, Cpu, Clock, Wrench, Package, Activity, ChevronRight, Tractor } from "lucide-react";
import useSWR from "swr";
import { getAll } from "../../api";
import SiteInfoCard from "./SiteInfoCard";

// ── Status helpers ─────────────────────────────────────────────────────────────

const machineStatusStyles = {
  DRAAIT:            { pill: "bg-green-100 text-green-700",  dot: "bg-green-500"  },
  GESTOPT:           { pill: "bg-red-100 text-red-600",      dot: "bg-red-500"    },
  ONDERHOUD:         { pill: "bg-yellow-100 text-yellow-700",dot: "bg-yellow-400" },
  NOOD_AAN_ONDERHOUD:{ pill: "bg-orange-100 text-orange-700",dot: "bg-orange-400" },
};

const prodStatusStyles = {
  GEZOND:      "text-green-600",
  FALEND:      "text-orange-600",
  PROBLEMEN:   "text-yellow-600",
  OFFLINE:     "text-red-500",
};

function StatusPill({ status, map }) {
  const s = map[status] ?? { pill: "bg-gray-100 text-gray-500" };
  return (
    <span className={`text-xs font-medium px-2 py-0.5 rounded-full ${s.pill}`}>
      {status}
    </span>
  );
}

// ── Locations list ─────────────────────────────────────────────────────────────

function LocationsList({ locations, selectedLocation, onSelect }) {
  return (
    <div className="bg-white rounded-2xl border border-gray-200 shadow-sm flex flex-col overflow-hidden w-72">
      <div className="px-4 pt-4 pb-2 shrink-0">
        <h3 className="font-bold text-gray-900 text-sm">Locaties op site</h3>
        <p className="text-xs text-gray-400 mt-0.5">
          {locations.length} locatie{locations.length !== 1 ? "s" : ""}
        </p>
      </div>
      <div className="overflow-y-auto flex-1 px-3 pb-3 space-y-1.5">
        {/* Reset-knop */}
        <button
          onClick={() => onSelect(null)}
          className={`w-full flex items-center justify-between rounded-xl border px-3 py-2.5 transition-all text-left ${
            selectedLocation === null
              ? "border-gray-900 bg-gray-50"
              : "border-gray-200 bg-white hover:border-gray-300"
          }`}
        >
          <div className="flex items-center gap-2">
            <MapPin size={13} className="text-gray-400 shrink-0" />
            <p className="text-sm font-semibold text-gray-800">Alle machines</p>
          </div>
          <ChevronRight
            size={14}
            className={`shrink-0 transition-colors ${
              selectedLocation === null ? "text-gray-900" : "text-gray-300"
            }`}
          />
        </button>

        {/* Scheidingslijn */}
        {locations.length > 0 && <hr className="border-gray-100" />}

        {locations.length === 0 && (
          <p className="text-xs text-gray-400 text-center mt-4">
            Geen locaties gevonden
          </p>
        )}
        {locations.map((loc) => {
          const isSelected = selectedLocation === loc.location;
          return (
            <button
              key={loc.location}
              onClick={() => onSelect(loc.location)}
              className={`w-full flex items-center justify-between rounded-xl border px-3 py-2.5 cursor-pointer transition-all text-left ${
                isSelected
                  ? "border-gray-900 bg-gray-50"
                  : "border-gray-200 bg-white hover:border-gray-300"
              }`}
            >
              <div className="flex items-center gap-2">
                <MapPin size={13} className="text-gray-400 shrink-0" />
                <div>
                  <p className="text-sm font-semibold text-gray-800">{loc.location}</p>
                  <p className="text-xs text-gray-400">
                    {loc.machineCount} machine{loc.machineCount !== 1 ? "s" : ""}
                  </p>
                </div>
              </div>
              <ChevronRight
                size={14}
                className={`shrink-0 transition-colors ${
                  isSelected ? "text-gray-900" : "text-gray-300"
                }`}
              />
            </button>
          );
        })}
      </div>
    </div>
  );
}

// ── Machines list ──────────────────────────────────────────────────────────────

function MachinesList({ machines, selectedMachineId, onSelect, location }) {
  return (
    <div className="bg-white rounded-2xl border border-gray-200 shadow-sm flex flex-col overflow-hidden w-72">
      <div className="px-4 pt-4 pb-2 flex items-start gap-3 shrink-0">
      <Tractor size={20} className="text-gray-400 mt-0.5 shrink-0" />
      <div className="min-w-0">
        <h3 className="font-bold text-gray-900 text-sm">Machines</h3>
        <p className="text-xs text-gray-400 mt-0.5">
          Locatie {location} · {machines.length} machine{machines.length !== 1 ? "s" : ""}
        </p>
      </div>
    </div>
      <div className="overflow-y-auto flex-1 px-3 pb-3 space-y-1.5">
        {machines.length === 0 && (
          <p className="text-xs text-gray-400 text-center mt-4">Geen machines op deze locatie</p>
        )}
        {machines.map((m) => {
          const isSelected = selectedMachineId === m.id;
          const s = machineStatusStyles[m.status] ?? { dot: "bg-gray-400" };
          return (
            <button
              key={m.id}
              onClick={() => onSelect(m)}
              className={`w-full flex items-center justify-between rounded-xl border px-3 py-2.5 cursor-pointer transition-all text-left ${
                isSelected
                  ? "border-gray-900 bg-gray-50"
                  : "border-gray-200 bg-white hover:border-gray-300"
              }`}
            >
              <div className="flex items-center gap-2 min-w-0">
                <span className={`w-2 h-2 rounded-full shrink-0 ${s.dot}`} />
                <div className="min-w-0">
                  <p className="text-sm font-semibold text-gray-800 truncate">{m.name}</p>
                  <p className={`text-xs font-medium ${prodStatusStyles[m.productieStatus] ?? "text-gray-400"}`}>
                    {m.productieStatus}
                  </p>
                </div>
              </div>
              <ChevronRight size={14} className={`shrink-0 transition-colors ${isSelected ? "text-gray-900" : "text-gray-300"}`} />
            </button>
          );
        })}
      </div>
    </div>
  );
}

// ── Machine detail ─────────────────────────────────────────────────────────────

function MachineDetail({ machine }) {
  if (!machine) {
    return (
      <div className="bg-white rounded-2xl border border-dashed border-gray-200 flex-1 flex items-center justify-center">
        <div className="text-center">
          <Package size={48} className="text-gray-200 mx-auto mb-2" />
          <p className="text-sm text-gray-400">Selecteer een machine</p>
        </div>
      </div>
    );
  }

  const rows = [
    {
      icon: <Activity size={16} className="text-gray-400 shrink-0" strokeWidth={1.5} />,
      label: "Werkingsstatus",
      value: <StatusPill status={machine.status} map={machineStatusStyles} />,
    },
    {
      icon: <Cpu size={16} className="text-gray-400 shrink-0" strokeWidth={1.5} />,
      label: "Productiestatus",
      value: (
        <span className={`text-sm font-semibold ${prodStatusStyles[machine.productieStatus] ?? "text-gray-700"}`}>
          {machine.productieStatus}
        </span>
      ),
    },
    {
      icon: <Package size={16} className="text-gray-400 shrink-0" strokeWidth={1.5} />,
      label: "Productinfo",
      value: <span className="text-sm text-gray-700">{machine.productinfo ?? "—"}</span>,
    },
    {
      icon: <Clock size={16} className="text-gray-400 shrink-0" strokeWidth={1.5} />,
      label: "Uptime",
      value: <span className="text-sm font-semibold text-gray-800">{machine.upTime ?? "—"} min</span>,
    },
    {
      icon: <Wrench size={16} className="text-gray-400 shrink-0" strokeWidth={1.5} />,
      label: "Laatste onderhoud",
      value: (
        <span className="text-sm text-gray-700">
          {machine.datumLaatsteOnderhoud
            ? new Date(machine.datumLaatsteOnderhoud).toLocaleDateString("nl-BE", {
                day: "2-digit", month: "short", year: "numeric",
              })
            : "—"}
        </span>
      ),
    },
  ];

  return (
    <div className="bg-white rounded-2xl border border-gray-200 shadow-sm flex-1 flex flex-col overflow-hidden">
      <div className="px-5 pt-5 pb-3 border-b border-gray-100">
        <div className="flex items-center gap-2 mb-1">
          <Tractor size={16} className="text-gray-400" strokeWidth={1.5} />
          <h3 className="font-bold text-gray-900 text-base">{machine.name}</h3>
        </div>
        <p className="text-xs text-gray-400">Machine details</p>
      </div>

      <div className="p-5 space-y-4 overflow-y-auto flex-1">
        {rows.map(({ icon, label, value }) => (
          <div key={label} className="flex items-center gap-3">
            {icon}
            <div className="flex items-center justify-between w-full gap-2">
              <p className="text-xs text-gray-400 shrink-0">{label}</p>
              <div>{value}</div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

// ── Main component ─────────────────────────────────────────────────────────────

export default function DashMachinePark({ selectedSite, onBack }) {
  const [selectedLocation, setSelectedLocation] = useState(null);
  const [selectedMachine, setSelectedMachine]   = useState(null);

  const { data: siteMachines = [], isLoading, error } = useSWR(
    selectedSite ? `sites/${selectedSite.id}/machines` : null,
    getAll,
  );

  const locations = Object.values(
    siteMachines.reduce((acc, machine) => {
      const loc = machine.location.locationOnSite;
      if (!acc[loc]) acc[loc] = { location: loc, machineCount: 0 };
      acc[loc].machineCount++;
      return acc;
    }, {}),
  ).sort((a, b) => a.location.localeCompare(b.location));

  // Geen locatie geselecteerd → toon alle machines
  const machinesOnLocation = selectedLocation
    ? siteMachines.filter((m) => m.location.locationOnSite === selectedLocation)
    : siteMachines;

  const handleLocationSelect = (loc) => {
    setSelectedLocation(loc);
    setSelectedMachine(null);
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-900">{selectedSite?.name} - Machinepark</h1>
        <button
          onClick={onBack}
          className="flex items-center gap-2 px-4 py-2 bg-gray-200 hover:bg-gray-300 text-gray-800 rounded-md transition-colors text-sm"
        >
          <ArrowLeft size={16} />
          Terug naar overzicht
        </button>
      </div>

      {isLoading ? (
        <p className="text-sm text-gray-400">Laden...</p>
      ) : (
        <div className="flex gap-4 items-start">
          {/* Kolom 1: infokaart */}
          <div className="flex flex-col gap-4">
            <SiteInfoCard site={selectedSite} />

          </div>  
          {/* Kolom 2: locaties */}
          <div>
            <LocationsList
              locations={locations}
              selectedLocation={selectedLocation}
              onSelect={handleLocationSelect}
            />
          </div>

          {/* Kolom 3: machines */}
          <MachinesList
            machines={machinesOnLocation}
            selectedMachineId={selectedMachine?.id}
            onSelect={setSelectedMachine}
            location={selectedLocation ?? "alle locaties"}
          />

          {/* Kolom 4: machinedetail */}
          <MachineDetail machine={selectedMachine} />
        </div>
      )}
    </div>
  );
}