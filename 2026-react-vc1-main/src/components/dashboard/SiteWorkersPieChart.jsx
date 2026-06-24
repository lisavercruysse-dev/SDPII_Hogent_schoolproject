// SiteWorkersPieChart.jsx
import DonutPieChart from "./DonutPieChart";

export default function SiteWorkersPieChart({
  workerCount, availableWorkers, afwezigheden,
  ziekteAfwezigheden, vakantieAfwezigheden,
}) {
  const otherAfwezig = Math.max(0, afwezigheden - ziekteAfwezigheden - vakantieAfwezigheden);

  return (
    <DonutPieChart
      title="Werknemersstatus"
      segments={[
        { name: "Beschikbaar", value: availableWorkers,    fill: "#10b981" },
        { name: "Ziekte",      value: ziekteAfwezigheden,  fill: "#ef4444" },
        { name: "Vakantie",    value: vakantieAfwezigheden, fill: "#f59e0b" },
        { name: "Overig",      value: otherAfwezig,         fill: "#6b7280" },
      ]}
      centerValue={availableWorkers}
      centerLabel={`van ${workerCount}`}
      footer={`Totaal werknemers: ${workerCount} • Afwezig: ${afwezigheden}`}
    />
  );
}