import useSWR from "swr";
import { getById } from "../../api";
import SiteInfoCard from "./SiteInfoCard";
import SiteWorkersPieChart from "./SiteWorkersPieChart";
import SiteTasksPieChart from "./SiteTasksPieChart"; 
import SiteMachinesPieChart from "./SiteMachinesPieChart";
import { ArrowRight } from "lucide-react";

export default function DashVerantwoordelijkeView({ site: plant, onDetails }) {

  const { data: workerStats } = useSWR(
    plant ? `sites/${plant.id}/worker-stats` : null,
    getById,
  );

  const { data: taskStats } = useSWR(
    plant ? `sites/${plant.id}/task-stats` : null,
    getById,
  );
  console.log("taskStats:" + taskStats);

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between m-4">
        <h1 className="text-2xl font-bold text-gray-900">{plant?.name}</h1>
        <button
          onClick={onDetails}
          className="px-4 py-2 bg-gray-200 hover:bg-gray-300 text-gray-800 rounded-md transition-colors"
        >
          Machinepark
          <ArrowRight size={18} className="inline-block ml-2" strokeWidth={1.5} />  
        </button>
      </div>

      <div className="grid grid-cols-[auto_1fr_1fr_1fr] gap-6 m-4 items-stretch">
          <SiteInfoCard site={plant} />
          <SiteWorkersPieChart
            workerCount={workerStats?.werknemerCount ?? 0}
            availableWorkers={workerStats?.availableWorkers ?? 0}
            afwezigheden={workerStats?.afwezigheden ?? 0}
            ziekteAfwezigheden={workerStats?.ziekteAfwezigheden ?? 0}
            vakantieAfwezigheden={workerStats?.vakantieAfwezigheden ?? 0}
            siteName={plant.name}
          />
          
          <SiteTasksPieChart 
              openTaken={taskStats?.openTaken ?? 0}
              toegewezenTaken={taskStats?.toegewezenTaken ?? 0}
            />

          <SiteMachinesPieChart siteId={plant?.id} />

      </div>
    </div>
  );
}