import SiteInfoCard from "./SiteInfoCard";

export default function DashVerantwoordelijkeDetail({ 
  plant, userData, onBack 
}) {
  
  return (
    <div className="space-y-6">
      {/* Header met terug-knop */}
      <div className="flex items-center justify-between m-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">
            {plant?.name}
          </h1>
        </div>
        <button onClick={onBack} 
          className="px-4 py-2 bg-gray-200 hover:bg-gray-300 text-gray-800 rounded-md transition-colors">
          Terug naar overzicht
        </button>
      </div>

      {/* Placeholder content */}
      <div className="w-full p-6 flex gap-8">
        <div className="w-full p-6 flex gap-8">
          {/* Linkerkant: infobox */}
          <div className="flex flex-col gap-4">
            <SiteInfoCard site={plant} userData={userData} />
          </div>
        </div>
      </div>
    </div>

  );
}