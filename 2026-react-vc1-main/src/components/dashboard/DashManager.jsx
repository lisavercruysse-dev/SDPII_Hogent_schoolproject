import { useState } from "react";
import DashManagerView from "./DashManagerView";
import DashMachinePark from "./DashMachinePark";

export default function DashManager() {
  const [view, setView] = useState("overview");
  const [selectedSite, setSelectedSite] = useState(null);

  return (
    <div className="min-h-screen bg-gray-50 m-2 p-6">
      {view === "overview" ? (
        <DashManagerView
          selectedSite={selectedSite}
          setSelectedSite={setSelectedSite}
          onDetails={() => setView("details")}
        />
      ) : (
        <DashMachinePark
          selectedSite={selectedSite}
          onBack={() => setView("overview")}
        />
      )}
    </div>
  );
}