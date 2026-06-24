// SiteMachinesPieChart.jsx
import useSWR from "swr";
import { getById } from "../../api";
import DonutPieChart from "./DonutPieChart";

export default function SiteMachinesPieChart({ siteId }) {
  const { data: stats } = useSWR(
    siteId ? `sites/${siteId}/machine-stats` : null,
    getById,
  );

  return (
    <DonutPieChart
      title="Machinestatus"
      segments={[
        { name: "Draait",           value: stats?.DRAAIT            ?? 0, fill: "#10b981" },
        { name: "Gestopt",          value: stats?.GESTOPT           ?? 0, fill: "#ef4444" },
        { name: "Nood aan onderhoud", value: stats?.NOOD_AAN_ONDERHOUD ?? 0, fill: "#f59e0b" },
        { name: "Onderhoud",        value: stats?.ONDERHOUD         ?? 0, fill: "#6b7280" },
      ]}
      centerValue={stats?.DRAAIT ?? 0} 
      centerLabel={`van ${stats?.total ?? 0}`}
      footer={`Totaal machines: ${stats?.total ?? 0}`}
    />
  );
}