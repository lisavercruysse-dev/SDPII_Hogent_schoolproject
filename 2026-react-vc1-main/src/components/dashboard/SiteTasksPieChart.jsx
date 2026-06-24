// SiteTasksPieChart.jsx
import DonutPieChart from "./DonutPieChart";

export default function SiteTasksPieChart({ openTaken, toegewezenTaken }) {
  const totaal = openTaken + toegewezenTaken;

  return (
    <DonutPieChart
      title="Open vs Afgewerkte Taken"
      segments={[
        { name: "Open",      value: openTaken,       fill: "#f59e0b" },
        { name: "Afgewerkt", value: toegewezenTaken, fill: "#10b981" },
      ]}
      centerValue={openTaken}
      centerLabel={`van ${totaal}`}
      footer={`Totaal taken: ${totaal}`}
    />
  );
}