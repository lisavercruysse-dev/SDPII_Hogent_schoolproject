import { FiSearch } from "react-icons/fi";
import TaskCard from "./TaskCard";
import img from "../../assets/boxlogo.png";

export function TaskCards({ tasks, searchQuery, setSearchQuery, onTaskDetailsClick, onCompleted, onCancel }) {
  return (
    <div className="border border-gray-200 rounded-[10px] flex flex-col w-full">
      <div className="border-b border-gray-200">
        <div className="min-h-19.25 max-h-19.25 flex mx-4 mt-4 justify-between items-start">
          <div>
            <p className="card-title mb-1">Mijn Taken</p>
            <p className="card-text">
              Markeer taken als afgewerkt wanneer ze klaar zijn
            </p>
          </div>
          <div className="flex gap-4">
            <input
              type="text"
              placeholder="omschrijving"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="border border-gray-200 rounded-md px-4 py-2 w-80 text-sm outline-none focus:border-gray-400"
            />
            <button className="bg-[#33333E] hover:bg-gray-800 text-white rounded-md px-4 py-2 flex items-center gap-2 text-sm transition-colors">
              <FiSearch />
              Zoeken
            </button>
          </div>
        </div>
      </div>

      <div className="divide-y divide-gray-200">
        {tasks.length ? (
          tasks.map((t) => (
            <TaskCard
              key={t.id}
              id={t.id}
              omschrijving={t.taakTemplate?.omschrijving ?? "Onbekend"}
              datum={t.datum}
              type={t.taakTemplate?.type ?? "Onbekend"}
              duurtijd={t.taakTemplate?.duurTijd ?? 0}
              status={t.status}
              task={t}
              onDetailsClick={() => onTaskDetailsClick(t)}
              onCompleted={() => onCompleted(t)}
              onCancel={() => onCancel(t)}
            />
          ))
        ) : (
          <div className="w-full flex flex-col items-center justify-center mt-10 mb-20">
            <img src={img} alt="niks gevonden" className="max-w-xs" data-cy="empty" />
            <p className="card-title text-center mt-4">Geen resultaten</p>
            <p className="card-text text-center mt-2">
              Er werden geen taken gevonden die aan je <br />
              zoekcriteria voldoen
            </p>
          </div>
        )}
      </div>
    </div>
  );
}
