export function DatePicker({ selectedDate, setSelectedDate }) {
  return (
    <div className="flex items-center gap-1.5 border border-gray-200 rounded-lg px-3 py-1.5 bg-white shadow-sm min-w-[170px]">
      
        <input
          data-cy="dateSelector"
          id="date-picker"
          type="date"
          value={selectedDate}
          onChange={(e) => setSelectedDate(e.target.value)}
          className="bg-transparent text-sm 
            outline-none cursor-pointer min-w-[110px]"
        />
      
    </div>
  );
}
