import UncompletedTask from "./UncompletedTask"

export default function UncompletedTaskList({tasks, onAssign}) {
  return (
    <div className="w-full text-gray-800 py-20">
      <p className="text-xl p-4 md:text-2xl font-bold">Onafgeronde taken</p>
      <div className="grid grid-cols-[2fr_1fr_1fr_1fr_200px] items-center py-3 border-y border-gray-200 text-sm font-medium">
        <p className="font-bold min-w-50">omschrijving</p>
        <p className="font-bold min-w-50">geschatte tijd</p>
        <p className="font-bold min-w-50">origineel gepland</p>
        <p className="font-bold min-w-50">type</p>
      </div>
      {tasks.map((t) => {
        return <UncompletedTask key={t.id} task={t} onAssign={onAssign}/>
      })}
    </div>
  )
}