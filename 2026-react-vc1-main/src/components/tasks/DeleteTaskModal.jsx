import { X } from 'lucide-react';

export default function DeleteTaskModal ({task, isOpen, onClose, onDelete}) {
  if (!isOpen) return null;
  return (
    <div className="fixed inset-0 bg-black/30 backdrop-blur-sm flex items-center justify-center z-50 p-4"
      onClick={onClose}>
      <div 
      className="bg-white rounded-xl shadow-xl w-full max-w-xl overflow-hidden flex flex-col"
      onClick={(e) => e.stopPropagation()}
      >
        <div className="p-6 flex flex-col gap-1 relative">
          <button
            className="absolute top-4 right-4 text-gray-400 hover:text-gray-600 transition-colors"
            onClick={onClose}
          >
            <X className="w-5 h-5" />
          </button>
        </div>
          <h2 className="p-5 self-center text-xl font-semibold text-gray-800">
            Taak Verwijderen
          </h2>
          <p className='w-sm p-5 text-center self-center text-[#717182]'>
            Weet je zeker dat je deze taak wil verwijderen? Dit kan niet ongedaan gemaakt worden.
          </p>
          <button className='m-5 self-end cursor-pointer px-4 py-2 bg-[#4863d6] text-white rounded-md text-sm font-medium hover:bg-[#3c52b3] transition-colors'
            onClick={() => onDelete(task.id)}
          >
            Bevestigen
          </button>
      </div>
    </div>
  )
}