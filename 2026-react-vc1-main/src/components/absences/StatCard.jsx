export default function StatCard({ label, value }) {
  return (
    <div
      data-cy="stat-card"
      className="bg-white border border-gray-200 rounded-xl p-6 flex flex-col items-center justify-center shadow-sm"
    >
      <span className="text-sm text-gray-500 mb-2">{label}</span>
      <span className="text-xl font-medium text-gray-800">{value}</span>
    </div>
  );
}
