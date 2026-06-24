// src/components/dashboard/DonutPieChart.jsx
import { PieChart, Pie } from "recharts";

export default function DonutPieChart({
  title,
  segments,
  centerValue,
  centerLabel,
  footer,
}) {
  const validSegments = segments.filter((s) => s.value > 0);
  const totalForPie = validSegments.reduce((sum, s) => sum + s.value, 0) || 1;

  return (
    <div className="bg-white rounded-2xl border border-gray-200 shadow-sm p-6 w-full flex flex-col items-center">
      <h3 className="font-semibold text-gray-800 text-base mb-4">{title}</h3>

      <div className="relative">
        <PieChart width={200} height={200}>
          <Pie
            data={validSegments}
            cx={100}
            cy={100}
            innerRadius={48}
            outerRadius={80}
            dataKey="value"
            startAngle={90}
            endAngle={-270}
            strokeWidth={4}
            stroke="#ffffff"
          />
        </PieChart>
        <div className="absolute inset-0 flex flex-col items-center justify-center pointer-events-none">
          <span className="text-3xl font-bold text-gray-800 leading-none">
            {centerValue}
          </span>
          <span className="text-[11px] text-slate-500 mt-1">{centerLabel}</span>
        </div>
      </div>

      <div className="w-full mt-6 grid gap-y-3 text-sm">
        {validSegments.map((seg, i) => (
          <div key={i} className="flex items-center gap-3">
            <div
              className="w-4 h-4 rounded-full shrink-0"
              style={{ backgroundColor: seg.fill }}
            />
            <span className="flex-1 font-medium text-gray-700">{seg.name}</span>
            <div className="text-right">
              <span className="font-semibold text-gray-800">{seg.value}</span>
              <span className="ml-1.5 text-xs text-gray-400">
                ({((seg.value / totalForPie) * 100).toFixed(0)}%)
              </span>
            </div>
          </div>
        ))}
      </div>

      {footer && (
        <p className="text-[12px] text-gray-400 mt-4">{footer}</p>
      )}
    </div>
  );
}