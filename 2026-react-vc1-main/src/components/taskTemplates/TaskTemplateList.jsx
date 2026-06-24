import { useState } from "react";
import { FiSearch } from "react-icons/fi";
import TaskTemplate from "./TaskTemplate";
import { useMemo } from "react";

export default function TaskTemplateList({ taskTemplates, onAssign }) {
  const [searchQuery, setSearchQuery] = useState("");

  const filteredTemplates = useMemo(() => {
    return taskTemplates.filter((template) => {
      const matchesSearch = template.omschrijving
      .toLowerCase().includes(searchQuery.toLowerCase());
   
    return matchesSearch;
    })

  })

  return (
    <div className="w-full text-gray-800">
      <p className="text-xl p-4 md:text-2xl font-bold">Taak templates</p>
      <div className="grid grid-cols-[2fr_1fr_1fr_200px] items-center py-3 border-y border-gray-200 text-sm font-medium">
        <p data-cy="taskTemplateOmschrijvingTitel" className="font-bold min-w-50">omschrijving</p>
        <p data-cy="taskTemplateMinutenTitel" className="font-bold min-w-50">geschatte tijd</p>
        <p data-cy="taskTemplateTypeTitel" className="font-bold min-w-50">type</p>

        <div className="flex gap-3 justify-end">
          <input data-cy="taskTemplateSearch"
            type="text"
            placeholder="omschrijving"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="border border-gray-200 rounded-md px-3 py-2 w-60 text-sm outline-none focus:border-gray-400"
          />

          <button className="bg-[#33333E] hover:bg-gray-800 text-white rounded-md px-4 py-2 flex items-center gap-2 text-sm transition-colors">
            <FiSearch />
            Zoeken
          </button>
        </div>
      </div>

      {filteredTemplates.map((task) => (
        <TaskTemplate data-cy="taskTemplateItem" key={task.id} taskTemplate={task} onAssign={onAssign} />
      ))}
    </div>
  );
}