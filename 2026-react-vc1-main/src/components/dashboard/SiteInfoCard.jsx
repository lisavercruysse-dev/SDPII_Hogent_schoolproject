import useSWR from "swr";
import { getById } from "../../api";
import { MapPin, Users, User, Settings, Clock, CheckSquare, 
  ListTodo, Tractor } from "lucide-react";

export default function SiteInfoCard({ site }) {
  if (!site) return null;

  const { data: detail, isLoading } = useSWR(`sites/${site.id}`, getById);

  const { data: machineStats } = useSWR(
    site ? `sites/${site.id}/machine-stats` : null,
    getById,
  );

  if (isLoading) return (
    <div className="bg-white rounded-2xl border border-gray-200 shadow-sm p-6 w-72 flex items-center justify-center">
      <p className="text-sm text-gray-400">Laden...</p>
    </div>
  );

  return (
    <div className="bg-white rounded-2xl border border-gray-200 shadow-sm p-5 w-72 flex flex-col gap-5">
      {/* Locatie */}
      <div className="flex items-start gap-3">
        <MapPin size={22} className="text-gray-400 mt-0.5 shrink-0" strokeWidth={1.5} />
        <div>
          <p className="font-semibold text-gray-800 text-sm">{site.name}</p>
          <p className="text-xs text-gray-400">{site.locatie}, {site.land}</p>
        </div>
      </div>

      {/* Werknemers */}
      <div className="flex items-center gap-3">
        <Users size={22} className="text-gray-400 shrink-0" strokeWidth={1.5} />
        <div>
          <p className="text-sm font-semibold text-gray-800">
            <span>{site.availableWorkers} / {site.werknemerCount}</span>
            <span className="text-xs text-gray-400"> werknemers</span>
          </p>
        </div>
      </div>

      <div className="flex items-center gap-3">
        <Tractor size={22} className="text-gray-400 shrink-0" strokeWidth={1.5} />
        <div>
          <p className="text-sm font-semibold text-gray-800">
            <span>{machineStats?.DRAAIT ?? "—"}</span>
            <span className="text-xs text-gray-400"> / {machineStats?.total ?? "—"} machines actief</span>
          </p>
        </div>
      </div>

      {/* Verantwoordelijke */}
      <div className="flex items-center gap-3">
        <User size={22} className="text-gray-400 shrink-0" strokeWidth={1.5} />
        <div>
          <p className="text-sm font-semibold text-gray-700">
            <span className="font-normal text-gray-400">Verantwoordelijke: </span>
            <br />
            <span>{detail.verantwoordelijke}</span>
          </p>
        </div>
      </div>

      {/* Status */}
      <div className="flex items-start gap-3">
        <Settings size={22} className="text-gray-400 mt-0.5 shrink-0" strokeWidth={1.5} />
        <div>
          <p className="text-sm text-gray-400">
            Operationele status:{" "}
            <span className={`font-semibold ${detail.operationeleStatus === "ACTIEF" ? "text-green-600" : "text-red-500"}`}>
              {detail.operationeleStatus}
            </span>
          </p>
          <p className="text-sm text-gray-400">
            Productiestatus:{" "}
            <span className={`font-semibold ${
              detail.siteProductieStatus === "GEZOND" ? "text-green-600"
              : detail.siteProductieStatus === "PROBLEMEN" ? "text-yellow-600"
              : "text-red-500"
            }`}>
              {detail.siteProductieStatus}
            </span>
          </p>
        </div>
      </div>

      {/* Gemiddelde taakduur */}
      <div className="flex items-center gap-3">
        <Clock size={22} className="text-gray-400 shrink-0" strokeWidth={1.5} />
        <div>
          <p className="text-xs text-gray-400">Gemiddelde duur taak voltooien</p>
          <p className="text-sm font-semibold text-gray-800">
            {detail.gemiddeldeVoltooiingstijd ?? "—"} minuten
          </p>
        </div>
      </div>

      {/* Geplande taken vandaag */}
      <div className="flex items-center gap-3">
        <ListTodo size={22} className="text-gray-400 shrink-0" strokeWidth={1.5} />
        <div>
          <p className="text-xs text-gray-400">Aantal geplande taken vandaag</p>
          <p className="text-sm font-semibold text-gray-800">{detail.geplandVandaag ?? "—"}</p>
        </div>
      </div>

      {/* Voltooide taken vandaag */}
      <div className="flex items-center gap-3">
        <CheckSquare size={22} className="text-gray-400 shrink-0" strokeWidth={1.5} />
        <div>
          <p className="text-xs text-gray-400">Aantal voltooide taken vandaag</p>
          <p className="text-sm font-semibold text-gray-800">{detail.afgewerktVandaag ?? "—"}</p>
        </div>
      </div>
    </div>
  );
}