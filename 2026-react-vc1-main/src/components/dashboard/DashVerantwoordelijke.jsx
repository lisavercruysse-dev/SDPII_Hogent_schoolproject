import { useAuth } from "../../contexts/auth";
import useSWR from "swr";
import { getById } from "../../api";
import { useState } from "react";
import DashVerantwoordelijkeView from "./DashVerantwoordelijkeView";
import DashMachinePark from "./DashMachinePark";

export default function DashVerantwoordelijke() {
  const { user } = useAuth();
  const [view, setView] = useState("dashboard");

  const { data: site, isLoading, error } = useSWR(
    user ? "sites/mine" : null,
    getById,
  );

  if (isLoading) {
    return <div className="p-8 text-sm text-gray-400">Laden...</div>;
  }

  if (error || !site) {
    return (
      <div className="p-8 text-center">
        <h2 className="text-xl font-semibold text-red-600">
          Geen site gekoppeld aan dit account
        </h2>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 m-4">
      {view === "dashboard" ? (
        <DashVerantwoordelijkeView
          site={site}
          onDetails={() => setView("details")}
        />
      ) : (
        <DashMachinePark
          selectedSite={site}
          onBack={() => setView("dashboard")}
        />
      )}
    </div>
  );
}