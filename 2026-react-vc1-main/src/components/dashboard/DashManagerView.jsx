import useSWR from "swr";
import { getById } from "../../api";
import MapAndSitesPanel from "./MapAndSitesPanel";
import SiteInfoCard from "./SiteInfoCard";
import NoSiteSelected from "./NoSiteSelected";
import SiteWorkersPieChart from "./SiteWorkersPieChart";
import SiteMachinesPieChart from "./SiteMachinesPieChart";
import SiteTasksPieChart from "./SiteTasksPieChart"; 

export default function DashManagerView({ selectedSite, setSelectedSite, onDetails }) {

  const { data: workerStats } = useSWR(
    selectedSite ? `sites/${selectedSite.id}/worker-stats` : null,
    getById,
  );

  const { data: taskStats } = useSWR(
    selectedSite ? `sites/${selectedSite.id}/task-stats` : null,
    getById,
  );

  return (
    <div className="space-y-6">
      <MapAndSitesPanel
        onSiteSelect={setSelectedSite}
        selectedSiteId={selectedSite?.id}
        onDetails={onDetails}
      />

      <div className="bg-white rounded-xl border border-gray-200 shadow-sm">
  {!selectedSite ? (
    <div className="h-135 flex items-center justify-center">
      <NoSiteSelected />
    </div>
  ) : (
    <div className="w-full px-6 py-3 grid grid-cols-[auto_1fr_1fr_1fr] gap-6 items-stretch">

            <SiteInfoCard site={selectedSite} />

            <SiteWorkersPieChart
              workerCount={workerStats?.werknemerCount ?? 0}
              availableWorkers={workerStats?.availableWorkers ?? 0}
              afwezigheden={workerStats?.afwezigheden ?? 0}
              ziekteAfwezigheden={workerStats?.ziekteAfwezigheden ?? 0}
              vakantieAfwezigheden={workerStats?.vakantieAfwezigheden ?? 0}
            />

            <SiteTasksPieChart
              openTaken={taskStats?.openTaken ?? 0}
              toegewezenTaken={taskStats?.toegewezenTaken ?? 0}
            />

            <SiteMachinesPieChart siteId={selectedSite?.id} />

          </div>
        )}
      </div>
    </div>
  );
}